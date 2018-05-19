package tools.storyteller;

import com.google.common.collect.ImmutableList;
import com.google.startupos.common.FileUtils;
import com.google.startupos.common.Logger;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import tools.storyteller.Protos.Config;
import tools.storyteller.service.Protos.Story;
import tools.storyteller.service.Protos.StoryItem;
import tools.storyteller.service.Protos.StoryList;


/* Storyteller writer that writes story files. */
@Singleton
public class StoryWriter {
  private static final Logger log = Logger.getForClass();
  private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss_z");

  private Config config;
  private FileUtils fileUtils;
  private StoryList.Builder storiesBuilder;
  private Story.Builder storyBuilder;
  private ImmutableList<StoryItem> storyItems;
  private long lastSavedStoryItemTimeMs;
  private long startStoryTimeMs;

  @Inject
  public StoryWriter(StorytellerConfig storytellerConfig, FileUtils fileUtils) {
    this.config = storytellerConfig.getConfig();
    this.fileUtils = fileUtils;
    storiesBuilder = StoryList.newBuilder();
    storyItems = ImmutableList.of();
  }

  void startStory(String project) {
    long currentTimeMS = getCurrentTimestamp();
    startStoryTimeMs = currentTimeMS;
    lastSavedStoryItemTimeMs = currentTimeMS;
    buildStory(project);
    storiesBuilder.addStory(storyBuilder);
    saveStories();
  }

  void updateStory(String project) {
    buildStory(project);
    updateCurrentStory();
    saveStories();
  }

  void endStory(String project) {
    buildStory(project);
    updateCurrentStory();
    saveStories();
    storyItems = ImmutableList.of();
  }

  void saveStoryItem(String project, String story) {
    // Replace empty with one space, so that the prototxt will have the entry
    story = story.isEmpty() ? " " : story;
    long currentTime = getCurrentTimestamp();
    long timeMs = currentTime - lastSavedStoryItemTimeMs;
    lastSavedStoryItemTimeMs = currentTime;
    StoryItem storyItem = StoryItem.newBuilder().setTimeMs(timeMs).setOneliner(story).build();
    ImmutableList<StoryItem> savedStoryItems = storyItems;
    storyItems = ImmutableList.<StoryItem>builder().addAll(savedStoryItems).add(storyItem).build();
    buildStory(project);
    updateCurrentStory();
    saveStories();
  }

  void saveScreenshot() {
    fileUtils.mkdirs(getUnsharedStoriesPath());
    Rectangle rectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    try {
      Robot robot = new Robot();
      BufferedImage screenshot = robot.createScreenCapture(rectangle);
      String filenameWithoutExtension =
          Paths.get(getUnsharedStoriesPath(), getCurrentTimeString()).toString();
      ImageIO.write(screenshot, "jpg", Paths.get(filenameWithoutExtension + ".jpg").toFile());
    } catch (AWTException | IOException e) {
      log.error("Error in saving screenshot", e);
    }
  }

  private void buildStory(String project) {
    storyBuilder = Story.newBuilder()
        .setStartTimeMs(startStoryTimeMs)
        .setEndTimeMs(getCurrentTimestamp())
        .setProject(project)
        .addAllItem(storyItems);
  }

  private void updateCurrentStory() {
    storiesBuilder.setStory(storiesBuilder.getStoryCount() - 1, storyBuilder.build());
  }

  private void saveStories() {
    fileUtils.writePrototxtUnchecked(
        storiesBuilder.build(),
        fileUtils.joinPaths(getUnsharedStoriesPath(), StorytellerConfig.STORIES_FILENAME));
  }

  private long getCurrentTimestamp() {
    return System.currentTimeMillis();
  }

  private String getCurrentTimeString() {
    return DATE_FORMATTER.format(new Date(System.currentTimeMillis()));
  }

  private String getUnsharedStoriesPath() {
    return Storyteller.getUnsharedStoriesPath(config);
  }
}
