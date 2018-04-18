package tools.storyteller;

import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite.Builder;
import com.google.startupos.common.FileUtils;
import com.google.startupos.common.Logger;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Optional;
import tools.storyteller.Protos.FileData;
import tools.storyteller.Protos.Story;
import tools.storyteller.Protos.StoryItem;
import tools.storyteller.Protos.ScreenshotMetadata;


/* Integrity verifier for story files. */
public class StoryVerifier {
  private static final Logger log = Logger.getForClass();

  private StoryVerifier() {}

  /* Check that:
   * - There are appropriate START and END files.
   * - Screenshots and metadata match.
   * Throws exception on error.
   */
  public static void checkFiles(ImmutableList<FileData> fileDataList) {
    boolean inStory = false;
    FileData lastStart = null;
    FileData lastEnd = null;
    for (FileData fileData : fileDataList) {
      if (fileData.getType() == FileData.Type.START) {
        if (inStory) {
          throw new IllegalStateException(
              String.format(
                  "Found 2 START files %s, %s with no END file between them",
                  lastStart.getFilename(), fileData.getFilename()));
        }
        inStory = true;
        lastStart = fileData;
      } else if (fileData.getType() == FileData.Type.END) {
        if (!inStory) {
          if (lastEnd != null) {
            throw new IllegalStateException(
                String.format(
                    "Found 2 END files %s, %s with no START file between them",
                    lastEnd.getFilename(), fileData.getFilename()));
          } else {
            throw new IllegalStateException(
                String.format(
                    "Found END file %s with no START file before it", fileData.getFilename()));
          }
        }
        inStory = false;
        lastEnd = fileData;
      } else if (fileData.getType() == FileData.Type.SCREENSHOT) {
        if (!inStory) {
          throw new IllegalStateException("Screenshot not in story:\n" + fileData);
        }
        Optional<FileData> metadataFileData =
            StoryReader.findFileData(
                fileDataList, FileData.Type.SCREENSHOT_METADATA, fileData.getTimeMs());
        if (!metadataFileData.isPresent()) {
          throw new IllegalStateException(
              "No metadata found for screenshot " + fileData.getFilename());
        }
      } else if (fileData.getType() == FileData.Type.SCREENSHOT) {
        if (!inStory) {
          throw new IllegalStateException("Metadata not in story:\n" + fileData);
        }
        Optional<FileData> screenshotData =
            StoryReader.findFileData(fileDataList, FileData.Type.SCREENSHOT, fileData.getTimeMs());
        if (!screenshotData.isPresent()) {
          throw new IllegalStateException(
              "No screenshot found for metadata " + fileData.getFilename());
        }
      }
    }
  }

  /* Checks stories:
   * - That they are chronologically consistent
   * - That they  contain all required data.
   * Throws exception on error.
   * Strict mode is used before generating an invoice.
   * TODO: Implement strict mode
   */
  public static void checkStories(ImmutableList<Story> stories, boolean strict) {
    long lastStoryTime = Long.MIN_VALUE;
    Story lastStory = null;
    long lastItemTime = Long.MIN_VALUE;
    StoryItem lastItem = null;
    for (Story story : stories) {
      if (lastStoryTime > story.getStartTimeMs()) {
        throw new IllegalStateException(
            String.format(
                "Stories start time should be in chronological order: %d, %d",
                lastStory.getStartTimeMs(), story.getStartTimeMs()));
      }
      if (story.getStartTimeMs() > story.getEndTimeMs()) {
        throw new IllegalStateException(
            String.format("Story start should be before story end:\n%s", story.toString()));
      }
      if (lastStory != null && lastStory.getEndTimeMs() > story.getStartTimeMs()) {
        throw new IllegalStateException(
            String.format(
                "Previous story end time should be before story start time: %d, %d",
                lastStory.getStartTimeMs(), story.getStartTimeMs()));
      }
      for (StoryItem item : story.getItemList()) {
        if (story.getStartTimeMs() > item.getTimeMs()) {
          throw new IllegalStateException(
              String.format(
                  "Story start time should be before item time: %d, %d",
                  story.getStartTimeMs(), item.getTimeMs()));
        }
        if (story.getEndTimeMs() < item.getTimeMs()) {
          throw new IllegalStateException(
              String.format(
                  "StoryItem time should be before story end time: %d, %d",
                  item.getTimeMs(), story.getEndTimeMs()));
        }
        if (lastItemTime > item.getTimeMs()) {
          throw new IllegalStateException(
              String.format(
                  "Story items should be in chronological order: %d, %d",
                  lastItemTime, item.getTimeMs()));
        }
      }
      lastItemTime = Long.MIN_VALUE;
      lastItem = null;
      lastStoryTime = story.getStartTimeMs();
      lastStory = story;
    }
  }
}

