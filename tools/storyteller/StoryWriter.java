package tools.storyteller;

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
  private Story.Builder storyBuilder;
  private StoryList.Builder storiesBuilder;
  private long timeMsLastSavedStoryItem;

  @Inject
  public StoryWriter(StorytellerConfig storytellerConfig, FileUtils fileUtils) {
    this.config = storytellerConfig.getConfig();
    this.fileUtils = fileUtils;
    storyBuilder = Story.newBuilder();
    storiesBuilder = StoryList.newBuilder();
  }

  public void writeStory(Storyteller.StorytellerStatus status, String project) {
    fileUtils.mkdirs(getUnsharedStoriesPath());
    int secondsShift = 0;
    switch (status) {
      case START: {
        timeMsLastSavedStoryItem = getCurrentTimestamp();
        secondsShift = -1;
        storyBuilder = Story.newBuilder();
        storyBuilder.setProject(project).setStartTimeMs(getCurrentTimestamp(secondsShift));
        if (timeMsLastSavedStoryItem == 0) {
          timeMsLastSavedStoryItem = getCurrentTimestamp();
        }
        break;
      }
      case RUNNING: {
        storyBuilder.setProject(project).setEndTimeMs(getCurrentTimestamp());
        break;
      }
      case END: {
        secondsShift = 1;
        long currentTime = getCurrentTimestamp(secondsShift);
        storyBuilder.setEndTimeMs(currentTime);
        timeMsLastSavedStoryItem = currentTime;
        storiesBuilder.addStory(storyBuilder.build());
        fileUtils.writePrototxtUnchecked(
            storiesBuilder.build(),
            fileUtils.joinPaths(getUnsharedStoriesPath(), StorytellerConfig.STORIES_FILENAME));
        storyBuilder = Story.newBuilder();
        break;
      }
    }
  }

  public void saveScreenshot(String project, String story) {
    fileUtils.mkdirs(getUnsharedStoriesPath());
    Rectangle rectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    try {
      Robot robot = new Robot();
      BufferedImage screenshot = robot.createScreenCapture(rectangle);
      String filenameWithoutExtension =
          Paths.get(getUnsharedStoriesPath(), getCurrentTimeString()).toString();
      ImageIO.write(screenshot, "jpg", Paths.get(filenameWithoutExtension + ".jpg").toFile());
      // Replace empty with one space, so that the prototxt will have the entry
      story = story.isEmpty() ? " " : story;
      long currentTime = getCurrentTimestamp();
      long timeMs = currentTime - timeMsLastSavedStoryItem;
      timeMsLastSavedStoryItem = currentTime;
      storyBuilder.setProject(project).addItem(StoryItem.newBuilder().setTimeMs(timeMs).setOneliner(story));
    } catch (AWTException | IOException e) {
      log.error("Error in saving screenshot", e);
    }
  }

  private long getCurrentTimestamp(int secondsShift) {
    return System.currentTimeMillis() + secondsShift * 1000;
  }

  private long getCurrentTimestamp() {
    return getCurrentTimestamp(0);
  }

  private String getCurrentTimeString() {
    return DATE_FORMATTER.format(new Date(System.currentTimeMillis()));
  }

  private String getUnsharedStoriesPath() {
    return Storyteller.getUnsharedStoriesPath(config);
  }
}
