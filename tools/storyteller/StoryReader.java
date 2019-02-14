package tools.storyteller;

import com.google.common.collect.ImmutableList;
import com.google.common.flogger.FluentLogger;
import com.google.protobuf.ByteString;
import com.google.startupos.common.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import tools.storyteller.Protos.Screenshot;
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

  ImmutableList<Story> getUnsharedStories(String absPath) {
    return getStories(absPath, StoriesState.UNSHARED);
  }

  ImmutableList<Story> getSharedStories(String absPath) {
    return getStories(absPath, StoriesState.SHARED);
  }

  private ImmutableList<Story> getStories(String absPath, StoriesState state) {
    StoryList.Builder storiesBuilder = StoryList.newBuilder();
    List<String> absFilenames = new ArrayList<>();
    if (state == StoriesState.UNSHARED) {
      String storiesAbsPath = fileUtils.joinPaths(absPath, StorytellerConfig.STORIES_FILENAME);
      if (fileUtils.fileExists(storiesAbsPath)) {
        absFilenames.add(storiesAbsPath);
      }
    } else {
      absFilenames = getAbsFilenamesFromFolderByExtension(absPath, "prototxt");
    }
    if (absFilenames.isEmpty()) {
      return ImmutableList.of();
    } else {
      absFilenames.forEach(
          path ->
              storiesBuilder.addAllStory(
                  ((StoryList) fileUtils.readPrototxtUnchecked(path, StoryList.newBuilder()))
                      .getStoryList()));
      return ImmutableList.copyOf(storiesBuilder.build().getStoryList());
    }
  }

  public ImmutableList<Screenshot> getScreenshots(String absPath) {
    ImmutableList.Builder<Screenshot> result = ImmutableList.builder();
    for (String absFilename : getAbsFilenamesFromFolderByExtension(absPath, "jpg")) {
      result.add(getScreenshot(absFilename));
    }
    return result.build();
  }

  private Screenshot getScreenshot(String absFilename) {
    Screenshot.Builder screenBuilder = Screenshot.newBuilder();
    File file = fileUtils.getFile(absFilename);
    long fileSize = file.length();
    if (fileSize < MAX_SCREENSHOT_SIZE_BYTES) {
      try {
        screenBuilder
            .setFilename(Paths.get(absFilename).getFileName().toString())
            .setScreenshot(ByteString.copyFrom(Files.readAllBytes(file.toPath())));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    } else {
      log.atSevere().log(
          "Screenshot size is %d bytes, %d bytes larger than maximum size. Path: %s",
          fileSize, fileSize - MAX_SCREENSHOT_SIZE_BYTES, absFilename);
    }
    return screenBuilder.build();
  }

  private ImmutableList<String> getAbsFilenamesFromFolderByExtension(
      String absPath, String extension) {
    try {
      return ImmutableList.copyOf(
          fileUtils
              .listContents(absPath)
              .stream()
              .filter(file -> file.endsWith("." + extension))
              .sorted()
              .map(filename -> fileUtils.joinPaths(absPath, filename))
              .collect(Collectors.toList()));
    } catch (IOException e) {
      throw new RuntimeException(
          String.format(
              "Can't read the files with %s extension properly by path: %s", extension, absPath));
    }
  }

  private enum StoriesState {
    SHARED,
    UNSHARED
  }
}

