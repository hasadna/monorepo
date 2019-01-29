package tools.storyteller;

import com.google.common.flogger.FluentLogger;
import com.google.startupos.common.FileUtils;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import tools.storyteller.Protos.Config;
import tools.storyteller.Protos.Story;
import tools.storyteller.Protos.StoryItem;
import tools.storyteller.Protos.StoryList;


/* Storyteller writer that writes story files. */
@Singleton
public class StoryWriter {
  private static final FluentLogger log = FluentLogger.forEnclosingClass();
  private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss_z");

  private final Config config;
  private final FileUtils fileUtils;
  private Story.Builder currentStoryBuilder;
  private List<Story> allStories;

  @Inject
  public StoryWriter(StorytellerConfig storytellerConfig, FileUtils fileUtils, StoryReader reader) {
    this.config = storytellerConfig.getConfig();
    this.fileUtils = fileUtils;
    this.allStories =
        new ArrayList<>(
            reader.getUnsharedStories(
                fileUtils.joinPaths(getUnsharedStoriesFolderPath()), false));
  }

  void startStory(String project) {
    currentStoryBuilder = Story.newBuilder();
    currentStoryBuilder.setStartTimeMs(getCurrentTimestamp()).setProject(project);
    allStories.add(currentStoryBuilder.build());
    saveStories();
  }

  void updateStory(String project) {
    if (currentStoryBuilder == null) {
      throw new IllegalStateException("'currentStoryBuilder' is not initialized");
    }
    updateCurrentStory(project);
    saveStories();
  }

  void endStory(String project) {
    if (currentStoryBuilder == null) {
      throw new IllegalStateException("'currentStoryBuilder' is not initialized");
    }
    updateCurrentStory(project);
    saveStories();
    currentStoryBuilder = Story.newBuilder();
  }

  void saveStoryItem(String project, String story) {
    // Replace empty with one space, so that the prototxt will have the entry
    story = story.isEmpty() ? " " : story;
    String screenshotFilename = saveScreenshot();
    StoryItem storyItem =
        StoryItem.newBuilder()
            .setTimeMs(getCurrentTimestamp())
            .setOneliner(story)
            .setScreenshotFilename(screenshotFilename)
            .build();
    currentStoryBuilder.setProject(project).setEndTimeMs(getCurrentTimestamp()).addItem(storyItem);
    allStories.set(allStories.size() - 1, currentStoryBuilder.build());
    saveStories();
  }

  // Returns filename of a saved screenshot
  String saveScreenshot() {
    fileUtils.mkdirs(getUnsharedStoriesFolderPath());
    Rectangle rectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    String filename = getCurrentTimeString() + ".jpg";
    try {
      Robot robot = new Robot();
      BufferedImage screenshot = robot.createScreenCapture(rectangle);
      ImageIO.write(screenshot, "jpg", Paths.get(getUnsharedStoriesFolderPath(), filename).toFile());
    } catch (AWTException | IOException e) {
      log.atSevere().withCause(e).log("Error in saving screenshot");
    }
    return filename;
  }

  void saveSharedStories(StoryList stories) throws IOException {
    final String sharedStoriesFolderPath = Storyteller.getSharedStoriesFolderPath(config);
    fileUtils.copyDirectoryToDirectory(
        getUnsharedStoriesFolderPath(),
        sharedStoriesFolderPath,
        StorytellerConfig.STORIES_FILENAME);
    fileUtils.writePrototxt(stories,
        fileUtils.joinPaths(sharedStoriesFolderPath, getCurrentTimeString() + ".prototxt"));
  }

  private void updateCurrentStory(String project) {
    currentStoryBuilder.setEndTimeMs(getCurrentTimestamp()).setProject(project);
    allStories.set(allStories.size() - 1, currentStoryBuilder.build());
  }

  private void saveStories() {
    fileUtils.writePrototxtUnchecked(
        StoryList.newBuilder().addAllStory(allStories).build(),
        fileUtils.joinPaths(getUnsharedStoriesFolderPath(), StorytellerConfig.STORIES_FILENAME));
  }

  private long getCurrentTimestamp() {
    return System.currentTimeMillis();
  }

  private String getCurrentTimeString() {
    return DATE_FORMATTER.format(new Date(System.currentTimeMillis()));
  }

  private String getUnsharedStoriesFolderPath() {
    return Storyteller.getUnsharedStoriesFolderPath(config);
  }
}

