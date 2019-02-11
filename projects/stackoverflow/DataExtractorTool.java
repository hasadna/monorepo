package projects.stackoverflow;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

public class DataExtractorTool {

  // paths of the files that we read from and write to
  private String inputFilePath;
  private String outputFilePath;

  private static final String ROW_TAG = "row";
  private static final String POSTS_TAG = "posts";
  // ROW_TAG attributes
  private static final String ID_ATTRIBUTE = "Id";
  private static final String POST_TYPE_ID_ATTRIBUTE = "PostTypeId";
  private static final String CREATION_DATE_ATTRIBUTE = "CreationDate";
  private static final String OWNER_USER_ID_ATTRIBUTE = "OwnerUserId";
  private static final String TAGS_ATTRIBUTE = "Tags";

  public void extractData() {
    // deffine those variables here so they can be cleaned in finally block
    XMLEventReader eventReader = null;
    XMLEventWriter eventWriter = null;
    try {
      // create XML file input stuff
      XMLInputFactory inputFactory = XMLInputFactory.newInstance();
      InputStream input = new FileInputStream(this.inputFilePath);
      eventReader = inputFactory.createXMLEventReader(input);
      // create XML file output file stuff
      XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
      FileOutputStream output = new FileOutputStream(this.outputFilePath);
      eventWriter = outputFactory.createXMLEventWriter(output);
      // read the file,and extract the relevant data from the row tag
      // and write other parts to the output file as they are.
      while (eventReader.hasNext()) {
        XMLEvent event = eventReader.nextEvent();
        if (isRowStartElement(event)) {
          // if this is the start of a row tag, then we want to
          // extract the relevant data, and write it to the output
          // file
          ExtractedRow currentRow = extractDataFromRow(event);
          writeExtractedRowToOutput(currentRow, eventWriter);
          continue;
        }
        if (isPostsStartElement(event)) {
          // for some reason, no characters event is fired for end of
          // line before the posts tag, so we have to add it, so the
          // extracted file looks more similar to the original one
          writeEndOfLine(eventWriter);
        }
        // else we have an event which we want to write it to the output
        // file as it is
        eventWriter.add(event);
      }
    } catch (XMLStreamException | FileNotFoundException e) {
      e.printStackTrace();
    } finally {
      // try to close all resources
      try {
        eventReader.close();
        eventWriter.close();
      } catch (XMLStreamException e) {
        e.printStackTrace();
      }
    }
  }

  private Boolean isRowStartElement(XMLEvent event) {
    // returns true if the event is a start element for a row tag
    // otherwise it returns false
    return event.isStartElement()
        && event.asStartElement().getName().getLocalPart().equals(ROW_TAG);
  }

  private Boolean isPostsStartElement(XMLEvent event) {
    return event.isStartElement()
        && event.asStartElement().getName().getLocalPart().equals(POSTS_TAG);
  }

  private ExtractedRow extractDataFromRow(XMLEvent event) {
    ExtractedRow currentRow = new ExtractedRow();
    Iterator<Attribute> attributes = event.asStartElement().getAttributes();
    while (attributes.hasNext()) {
      Attribute attribute = attributes.next();
      String attributeName = attribute.getName().toString();
      switch (attributeName) {
      case ID_ATTRIBUTE:
        currentRow.setId(attribute.getValue());
        break;
      case POST_TYPE_ID_ATTRIBUTE:
        currentRow.setPostTypeId(attribute.getValue());
        break;
      case CREATION_DATE_ATTRIBUTE:
        currentRow.setCreationDate(attribute.getValue());
        break;
      case OWNER_USER_ID_ATTRIBUTE:
        currentRow.setOwnerUserId(attribute.getValue());
        break;
      case TAGS_ATTRIBUTE:
        currentRow.setTags(attribute.getValue());
        break;
      }
    }
    return currentRow;
  }

  private void writeExtractedRowToOutput(ExtractedRow currentRow,
      XMLEventWriter writer) throws XMLStreamException {
    XMLEventFactory eventFactory = XMLEventFactory.newInstance();
    XMLEvent rowStartElement = eventFactory.createStartElement("", "", ROW_TAG);
    writer.add(rowStartElement);
    XMLEvent idAttrib = eventFactory.createAttribute(ID_ATTRIBUTE,
        currentRow.getId());
    writer.add(idAttrib);
    XMLEvent postTypeIdAttrib = eventFactory.createAttribute(
        POST_TYPE_ID_ATTRIBUTE, currentRow.getPostTypeId());
    writer.add(postTypeIdAttrib);
    XMLEvent creationDateAttrib = eventFactory.createAttribute(
        CREATION_DATE_ATTRIBUTE, currentRow.getCreationDate());
    writer.add(creationDateAttrib);
    // OwnerUserId attribute is not always available, so, add it if it only
    // is
    // available.
    if (currentRow.getOwnerUserId() != null) {
      XMLEvent ownerUserIdAttrib = eventFactory.createAttribute(
          OWNER_USER_ID_ATTRIBUTE, currentRow.getOwnerUserId());
      writer.add(ownerUserIdAttrib);
    }
    // tags attribute is not always available, so add it only if it is
    // available.
    if (currentRow.getTags() == null) {
      return;
    }
    XMLEvent tagsAttrib = eventFactory.createAttribute(TAGS_ATTRIBUTE,
        currentRow.getTags());
    writer.add(tagsAttrib);
    return;
  }

  private void writeEndOfLine(XMLEventWriter writer) throws XMLStreamException {
    XMLEventFactory eventFactory = XMLEventFactory.newInstance();
    XMLEvent endOfLine = eventFactory.createCharacters("\n");
    writer.add(endOfLine);
    return;
  }

  private void setInputFilePath(String inputFilePath) {
    this.inputFilePath = inputFilePath;
  }

  private void setOutputFilePath(String outputFilePath) {
    this.outputFilePath = outputFilePath;
  }

  public static void main(String[] args) {
    DataExtractorTool extractor = new DataExtractorTool();
    extractor.setInputFilePath("Posts.xml");
    extractor.setOutputFilePath("Posts-extracted.xml");
    extractor.extractData();
  }
}
