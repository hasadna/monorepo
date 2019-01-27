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
            reader.getStories(
                fileUtils.joinPaths(getUnsharedStoriesPath(), StorytellerConfig.STORIES_FILENAME)));
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
    StoryItem storyItem =
        StoryItem.newBuilder().setTimeMs(getCurrentTimestamp()).setOneliner(story).build();
    currentStoryBuilder.setProject(project).setEndTimeMs(getCurrentTimestamp()).addItem(storyItem);
    allStories.set(allStories.size() - 1, currentStoryBuilder.build());
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
      log.atSevere().withCause(e).log("Error in saving screenshot");
    }
  }

  void saveSharedStories(StoryList stories) throws IOException {
    final String sharedStoriesPath = Storyteller.getSharedStoriesPath(config);
    fileUtils.copyDirectoryToDirectory(
        getUnsharedStoriesPath(),
        sharedStoriesPath,
        StorytellerConfig.STORIES_FILENAME);
    fileUtils.writePrototxt(stories,
        fileUtils.joinPaths(sharedStoriesPath, getCurrentTimeString() + ".prototxt"));
  }

  private void updateCurrentStory(String project) {
    currentStoryBuilder.setEndTimeMs(getCurrentTimestamp()).setProject(project);
    allStories.set(allStories.size() - 1, currentStoryBuilder.build());
  }

  private void saveStories() {
    fileUtils.writePrototxtUnchecked(
        StoryList.newBuilder().addAllStory(allStories).build(),
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

