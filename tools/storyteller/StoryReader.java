package tools.storyteller;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.ByteString;
import com.google.startupos.common.FileUtils;
import com.google.common.flogger.FluentLogger;

import javax.inject.Inject;
import javax.inject.Singleton;

import tools.storyteller.Protos.Story;
import tools.storyteller.Protos.StoryList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/* Storyteller reader that reads story files into a Story proto. */
@Singleton
public class StoryReader {
  private static final FluentLogger log = FluentLogger.forEnclosingClass();
  // Maximum size for a document in Firestore is 1,048,576 bytes. We reserve 10,240 bytes for other fields.
  private static final int MAX_SCREENSHOT_SIZE_BYTES = 1038336;

  private FileUtils fileUtils;

  @Inject
  public StoryReader(FileUtils fileUtils) {
    this.fileUtils = fileUtils;
  }

  ImmutableList<Story> getStoriesWithScreenshots(String path) {
    String storiesFolderPath = path.substring(0, path.lastIndexOf("/"));
    if (!fileUtils.fileExists(path)) {
      return ImmutableList.of();
    }
    StoryList savedStories = getStoryList(path);
    StoryList.Builder storiesBuilder = savedStories.toBuilder();
    for (Protos.Story.Builder story : storiesBuilder.getStoryBuilderList()) {
      for (Protos.StoryItem.Builder storyItem : story.getItemBuilderList()) {
        storyItem
            .setScreenshot(getScreenshotInByteString(
                fileUtils.joinPaths(storiesFolderPath,
                    storyItem.getScreenshotFilename())));
      }
    }
    return ImmutableList.copyOf(storiesBuilder.build().getStoryList());
  }

  ImmutableList<Story> getStories(String path) {
    if (!fileUtils.fileExists(path)) {
      return ImmutableList.of();
    }
    return ImmutableList.copyOf(getStoryList(path).getStoryList());
  }

  private StoryList getStoryList(String path) {
    return (StoryList) fileUtils.readPrototxtUnchecked(path, StoryList.newBuilder());
  }

  private ByteString getScreenshotInByteString(String screenshotPath) {
    ByteString result = ByteString.EMPTY;
    File file = new File(screenshotPath);
    long fileSize = file.length();
    if (fileSize < MAX_SCREENSHOT_SIZE_BYTES) {
      try {
        result = ByteString.copyFrom(Files.readAllBytes(file.toPath()));
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      log.atSevere().log(
          "Size ot %s file is too much: %s bytes. Max supported size: %s bytes",
          screenshotPath,
          fileSize,
          MAX_SCREENSHOT_SIZE_BYTES);
    }
    return result;
  }
}

