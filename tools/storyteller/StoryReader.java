package tools.storyteller;

import com.google.common.collect.ImmutableList;
import com.google.common.flogger.FluentLogger;
import com.google.protobuf.ByteString;
import com.google.startupos.common.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import tools.storyteller.Protos.Story;
import tools.storyteller.Protos.StoryList;

/* Storyteller reader that reads story files into a Story proto. */
@Singleton
public class StoryReader {
  private static final FluentLogger log = FluentLogger.forEnclosingClass();
  // Maximum size for a document in Firestore is 1,048,576 bytes.
  // We reserve 10,240 bytes for other fields.
  private static final int MAX_SCREENSHOT_SIZE_BYTES = 1038336;

  private FileUtils fileUtils;

  @Inject
  public StoryReader(FileUtils fileUtils) {
    this.fileUtils = fileUtils;
  }

  ImmutableList<Story> getUnsharedStories(String absPath, boolean loadScreenshots) {
    return getStories(absPath, loadScreenshots, StoriesState.UNSHARED);
  }

  ImmutableList<Story> getSharedStories(String absPath, boolean loadScreenshots) {
    return getStories(absPath, loadScreenshots, StoriesState.SHARED);
  }

  private ImmutableList<Story> getStories(
      String absPath, boolean loadScreenshots, StoriesState state) {
    ImmutableList.Builder<Story> result = ImmutableList.builder();
    List<String> absPaths = new ArrayList<>();
    try {
      if (state.equals(StoriesState.UNSHARED)) {
        String storiesAbsPath = fileUtils.joinPaths(absPath, StorytellerConfig.STORIES_FILENAME);
        if (fileUtils.fileExists(storiesAbsPath)) {
          absPaths.add(storiesAbsPath);
        }
      } else {
        absPaths = fileUtils
            .listContents(absPath)
            .stream()
            .filter(file -> file.endsWith(".prototxt"))
            .sorted()
            .map(filename -> fileUtils.joinPaths(absPath, filename))
            .collect(Collectors.toList());
      }
    } catch (IOException e) {
      throw new RuntimeException("Can't read the stories properly by path: " + absPath);
    }
    if (absPaths.isEmpty()) {
      return result.build();
    } else {
      absPaths.forEach(
          path
              -> result.addAll(
              ((StoryList) fileUtils.readPrototxtUnchecked(
                  path, StoryList.newBuilder())).getStoryList()));
      if (!loadScreenshots) {
        return result.build();
      } else {
        return addScreenshots(new ArrayList<>(result.build()), absPath);
      }
    }
  }

  private ImmutableList<Story> addScreenshots(
      List<Story> stories, String absPath) {
    List<Story.Builder> result = stories
        .stream()
        .map(Story::toBuilder)
        .collect(Collectors.toList());
    for (Protos.Story.Builder story : result) {
      for (Protos.StoryItem.Builder storyItem : story.getItemBuilderList()) {
        storyItem
            .setScreenshot(readScreenshot(
                fileUtils.joinPaths(absPath, storyItem.getScreenshotFilename())));
      }
    }
    return ImmutableList.copyOf(
        result
            .stream()
            .map(Story.Builder::build)
            .collect(Collectors.toList()));
  }

  private ByteString readScreenshot(String absFilename) {
    ByteString result = ByteString.EMPTY;
    File file = fileUtils.getFile(absFilename);
    long fileSize = file.length();
    if (fileSize < MAX_SCREENSHOT_SIZE_BYTES) {
      try {
        result = ByteString.copyFrom(Files.readAllBytes(file.toPath()));
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      log.atSevere().log(
          "Screenshot size is %d bytes, %d bytes larger than maximum size. Path: %s",
          fileSize,
          fileSize - MAX_SCREENSHOT_SIZE_BYTES,
          absFilename);
    }
    return result;
  }

  private enum StoriesState {
    SHARED,
    UNSHARED
  }
}

