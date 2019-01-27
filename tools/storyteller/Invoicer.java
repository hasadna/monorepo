package tools.storyteller;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.startupos.common.FileUtils;
import com.google.startupos.common.Time;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import tools.storyteller.Protos.InvoiceCreator;
import tools.storyteller.Protos.InvoiceItem;
import tools.storyteller.Protos.InvoiceItemList;
import tools.storyteller.Protos.InvoiceReceiver;
import tools.storyteller.Protos.Story;

/* Invoicer - a class that generates invoice PDFs.
 *
 * Usage:
 * Invoicer.saveInvoice(...)
 */
public class Invoicer {
  private static PDFont FONT = PDType1Font.HELVETICA;
  private static PDFont BOLD_FONT = PDType1Font.HELVETICA_BOLD;
  private static PDPage PAGE = new PDPage(PDRectangle.A4);
  private static int MAX_X = (int) PAGE.getArtBox().getWidth();
  private static int MAX_Y = (int) PAGE.getArtBox().getHeight();
  private static int TOP = 30;
  private static int LEFT = 40;

  private static int SMALL_SIZE = 10;
  private static int NORMAL_SIZE = 11;
  private static int LARGE_SIZE = 14;
  private static boolean BOLD = true;
  private static boolean NOT_BOLD = false;
  private static PDColor DEFAULT_COLOR = null;
  private static PDColor BLACK = getColor(0);
  private static PDColor WHITE = getColor(255);
  private static PDColor GRAY = getColor(150);
  private static PDColor LIGHT_GRAY = getColor(220);
  private static PDColor DARK_GRAY = getColor(120);
  private static PDColor GREEN = getColor(77, 173, 86);

  private PDDocument document;
  private PDPageContentStream content;
  private PDColor defaultColor;

  private InvoiceCreator creator;
  private InvoiceReceiver receiver;
  private InvoiceItemList items;
  private long invoiceNumber;
  private long timeOfIssueMs;

  @Inject FileUtils fileUtils;

  private Invoicer(
      InvoiceCreator creator,
      InvoiceReceiver receiver,
      InvoiceItemList items,
      long invoiceNumber,
      long timeOfIssueMs) {
    document = new PDDocument();
    document.addPage(PAGE);
    this.creator = creator;
    this.receiver = receiver;
    this.items = items;
    this.invoiceNumber = invoiceNumber;
    this.timeOfIssueMs = timeOfIssueMs;
    defaultColor = BLACK;
  }

  public static void saveInvoice(
      String path,
      InvoiceCreator creator,
      InvoiceReceiver receiver,
      ImmutableList<Story> stories,
      long startTimeMs,
      long endTimeMs,
      long invoiceNumber,
      long timeOfIssueMs)
      throws IOException {
    InvoiceItemList items =
        getInvoiceItems(stories, startTimeMs, endTimeMs, creator.getHourlyRateUsd());
    Invoicer invoicer = new Invoicer(creator, receiver, items, invoiceNumber, timeOfIssueMs);
    invoicer.renderInvoice();
    invoicer.saveInvoice(path);
    invoicer.close();
  }

  void saveInvoice(String path) throws IOException {
    fileUtils.mkdirs(path);
    document.save(path);
  }

  void close() throws IOException {
    document.close();
  }

  void renderInvoice() throws IOException {
    content = new PDPageContentStream(document, PAGE);
    addHeader();
    addHorizontalLine(285, 2, GREEN);
    addTable();
    addFooter();
    content.close();
  }

  void addHeader() throws IOException {
    int lineLength = 16;
    addRect(0, 0, MAX_X, 103);
    setDefaultColor(WHITE);
    write("INVOICE", LEFT, TOP + 25, NOT_BOLD, 40, DEFAULT_COLOR);

    List<String> texts =
        new ArrayList<>(Arrays.asList(creator.getName(), creator.getEmail(), creator.getWebsite()));
    texts.removeIf(str -> Strings.nullToEmpty(str).trim().isEmpty());
    write(ImmutableList.copyOf(texts), LEFT + 270, TOP);

    texts =
        new ArrayList<>(
            Arrays.asList(
                creator.getPhoneNumber(),
                creator.getStreetAndNumber(),
                creator.getCityStateCountry(),
                creator.getZipCode()));
    texts.removeIf(str -> Strings.nullToEmpty(str).trim().isEmpty());
    write(ImmutableList.copyOf(texts), LEFT + 430, TOP);

    setDefaultColor(BLACK);

    int billedToY = TOP + 115;
    int dateOfIssueY = billedToY + 40;
    final int invoicePeriodY = dateOfIssueY + 40;
    write("Billed To", LEFT, billedToY, BOLD, SMALL_SIZE, GRAY);
    write("Invoice Number", LEFT + 200, billedToY, BOLD, SMALL_SIZE, GRAY);
    write("Date Of Issue", LEFT + 200, dateOfIssueY, BOLD, SMALL_SIZE, GRAY);
    write("Invoice period", LEFT + 200, invoicePeriodY, BOLD, SMALL_SIZE, GRAY);
    write("Invoice total", MAX_X - LEFT, billedToY, BOLD, SMALL_SIZE, GRAY, true);

    texts =
        new ArrayList<>(
            Arrays.asList(
                receiver.getName(), receiver.getStreetAndNumber(), receiver.getCityStateCountry()));
    texts.removeIf(str -> Strings.nullToEmpty(str).trim().isEmpty());
    write(ImmutableList.copyOf(texts), LEFT + 2, TOP + 135, NOT_BOLD, SMALL_SIZE, BLACK, false);
    write(
        String.valueOf(invoiceNumber), LEFT + 200 + 2, billedToY + 20, NOT_BOLD, SMALL_SIZE, BLACK);
    write(getDate(timeOfIssueMs), LEFT + 200 + 2, dateOfIssueY + 20, NOT_BOLD, SMALL_SIZE, BLACK);
    write(
        getDate(items.getStartTimeMs()) + " - " + getDate(items.getEndTimeMs()),
        LEFT + 200 + 2,
        invoicePeriodY + 20,
        NOT_BOLD,
        SMALL_SIZE,
        BLACK);

    write("$" + items.getTotal(), MAX_X - LEFT, TOP + 150, NOT_BOLD, 32, GREEN, true);
  }

  String getDate(long timeMs) {
    return Time.format("d MMM y", timeMs);
  }

  void write(ImmutableList<String> texts, int x, int y) throws IOException {
    write(texts, x, y, NOT_BOLD, NORMAL_SIZE, DEFAULT_COLOR, false);
  }

  void write(String text, int x, int y, boolean bold, int fontSize, PDColor color)
      throws IOException {
    write(ImmutableList.of(text), x, y, bold, fontSize, color, false);
  }

  void write(
      String text, int x, int y, boolean bold, int fontSize, PDColor color, boolean rightAlign)
      throws IOException {
    write(ImmutableList.of(text), x, y, bold, fontSize, color, rightAlign);
  }

  void write(
      ImmutableList<String> texts,
      int x,
      int y,
      boolean bold,
      int fontSize,
      PDColor color,
      boolean rightAlign)
      throws IOException {
    final PDColor currentDefaultColor = defaultColor;
    PDFont font = bold ? BOLD_FONT : FONT;
    if (color != null) {
      setDefaultColor(color);
    }
    content.beginText();
    content.setFont(font, fontSize);
    content.newLineAtOffset(x, MAX_Y - y);
    for (String text : texts) {
      if (rightAlign) {
        final float text_width = (font.getStringWidth(text) / 1000.0f) * fontSize;
        content.moveTextPositionByAmount(-text_width, 0);
        content.showText(text);
        content.moveTextPositionByAmount(text_width, 0);
      } else {
        content.showText(text);
      }
      content.newLineAtOffset(0, -18);
    }
    content.endText();
    setDefaultColor(currentDefaultColor);
  }

  void addRect(int x, int y, int width, int height) throws IOException {
    final PDColor currentDefaultColor = defaultColor;
    setDefaultColor(GREEN);
    content.addRect(x, MAX_Y - y - height, width, height);
    content.fill();
    content.setNonStrokingColor(BLACK);
    setDefaultColor(currentDefaultColor);
  }

  private void addHorizontalLine(int y, double width, PDColor color) throws IOException {
    content.setLineWidth((float) width);
    content.setStrokingColor(color);
    content.moveTo(LEFT - 3, MAX_Y - y);
    content.lineTo(MAX_X - LEFT + 3, MAX_Y - y);
    content.stroke();
  }

  void addTable() throws IOException {
    final int col1x = LEFT;
    final int col2x = LEFT + 300;
    final int col3x = LEFT + 415;
    final int col4x = LEFT + 513;
    final int summaryX = LEFT + 440;
    int top = TOP + 280;
    // Add table headers
    write("Description", col1x, top, BOLD, NORMAL_SIZE, GREEN);
    write("Time", col2x, top, BOLD, NORMAL_SIZE, GREEN, true);
    write("Hourly rate", col3x, top, BOLD, NORMAL_SIZE, GREEN, true);
    write("Amount", col4x, top, BOLD, NORMAL_SIZE, GREEN, true);

    // Add table
    int posY = top + 25;
    for (InvoiceItem item : items.getItemList()) {
      write(item.getTitle(), col1x, posY, NOT_BOLD, NORMAL_SIZE, BLACK);
      write(item.getDescription(), col1x, posY + 15, NOT_BOLD, 8, DARK_GRAY);
      write(
          Time.getHourMinuteDurationString(item.getTimeWorkedMs()),
          col2x,
          posY,
          NOT_BOLD,
          NORMAL_SIZE,
          BLACK,
          true);
      write("$" + item.getHourlyRate(), col3x, posY, NOT_BOLD, NORMAL_SIZE, BLACK, true);
      write(
          String.format("$%.2f", item.getDollarAmount()),
          col4x,
          posY,
          NOT_BOLD,
          NORMAL_SIZE,
          BLACK,
          true);
      addHorizontalLine(posY + 25, 0.6, LIGHT_GRAY);
      posY += 45;
    }

    // Add table summary
    double roundUp = roundToCent(items.getTotal() - items.getSubtotal());
    ImmutableList summaryLabels =
        ImmutableList.of("Subtotal", "Rounding up", "Total", "", "Amount Due (USD)");
    ImmutableList summaryValues =
        ImmutableList.of(
            "$" + roundToCent(items.getSubtotal()),
            "$" + roundUp,
            "$" + roundToCent(items.getTotal()),
            "",
            "$" + roundToCent(items.getTotal()));
    if (roundUp == 0) {
      summaryLabels = ImmutableList.of("Total", "", "Amount Due (USD)");
      summaryValues = ImmutableList.of("$" + items.getTotal(), "", "$" + items.getTotal());
    }
    posY += 30;
    write(summaryLabels, summaryX, posY, BOLD, NORMAL_SIZE, GREEN, true);
    write(summaryValues, col4x, posY, NOT_BOLD, NORMAL_SIZE, BLACK, true);
  }

  void addFooter() throws IOException {
    String text = creator.getTagline();
    if (!Strings.isNullOrEmpty(text)) {
      int fontSize = 14;
      final float text_width = (BOLD_FONT.getStringWidth(text) / 1000.0f) * fontSize;
      write(text, (int) ((MAX_X + text_width) / 2), MAX_Y - 40, BOLD, fontSize, GRAY, true);
    }
  }

  void setDefaultColor(PDColor color) throws IOException {
    defaultColor = color;
    content.setNonStrokingColor(color);
  }

  static PDColor getColor(int r, int g, int b) {
    float[] components = new float[] {r / 255f, g / 255f, b / 255f};
    return new PDColor(components, PDDeviceRGB.INSTANCE);
  }

  static PDColor getColor(int grayscale) {
    float[] components = new float[] {grayscale / 255f, grayscale / 255f, grayscale / 255f};
    return new PDColor(components, PDDeviceRGB.INSTANCE);
  }

  private static InvoiceItemList getInvoiceItems(
      ImmutableList<Story> stories, long startTimeMs, long endTimeMs, double hourlyRate) {
    InvoiceItemList.Builder result = InvoiceItemList.newBuilder();
    result.setStartTimeMs(startTimeMs);
    result.setEndTimeMs(endTimeMs);

    // Sum time worked per project
    Map<String, Long> map = new LinkedHashMap<>();
    for (Story story : stories) {
      String project = story.getProject();
      long previousTimeMs = map.containsKey(project) ? map.get(project) : 0;
      long timeDeltaMs = story.getEndTimeMs() - story.getStartTimeMs();
      map.put(project, previousTimeMs + timeDeltaMs);
    }

    // Create invoice items
    double subtotal = 0;
    for (Map.Entry<String, Long> item : map.entrySet()) {
      // TODO: Wrap guava Strings so we can use both
      String project = com.google.startupos.common.Strings.capitalize(item.getKey()) + " project";
      double perMinuteRate = hourlyRate / 60;
      double timeWorkedMinutes = item.getValue() / (1000 * 60);
      double dollarAmount = timeWorkedMinutes * perMinuteRate;
      dollarAmount = roundToCent(dollarAmount);
      subtotal += dollarAmount;
      result.addItem(
          InvoiceItem.newBuilder()
              .setTitle(project)
              .setDescription("Software design, development and testing")
              .setTimeWorkedMs(item.getValue())
              .setHourlyRate(hourlyRate)
              .setDollarAmount(dollarAmount)
              .build());
    }
    double total = roundToDollar(subtotal);
    result.setSubtotal(subtotal);
    result.setTotal(total);
    return result.build();
  }

  static double roundToDollar(double value) {
    return Math.ceil(value * 1) / 1.0;
  }

  static double roundToCent(double value) {
    return Math.ceil(value * 100) / 100.0;
  }
}

