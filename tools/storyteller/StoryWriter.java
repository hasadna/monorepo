package tools.storyteller;

import com.google.protobuf.Message;
import com.google.startupos.common.FileUtils;
import com.google.startupos.common.Logger;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import tools.storyteller.Protos.Config;
import tools.storyteller.Protos.FileData;
import tools.storyteller.Protos.ScreenshotMetadata;


/* Storyteller writer that writes story files. */
@Singleton
public class StoryWriter {
  private static final Logger log = Logger.getForClass();
  public static DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss_z");

  private Config config;
  private FileUtils fileUtils;

  @Inject
  public StoryWriter(StorytellerConfig storytellerConfig, FileUtils fileUtils) {
    this.config = storytellerConfig.getConfig();
    this.fileUtils = fileUtils;
  }

  private String getSharedStoriesPath() {
    return Storyteller.getSharedStoriesPath(config);
  }

  private String getUnsharedStoriesPath() {
    return Storyteller.getUnsharedStoriesPath(config);
  }

  public void writeStatusFile(FileData.Type type, Message contents) {
    fileUtils.mkdirs(getUnsharedStoriesPath());
    // We shift by a second to screenshots will be within START and END.
    int secondsShift = 0;
    if (type == FileData.Type.START) {
      secondsShift = -1;
    }
    if (type == FileData.Type.END) {
      secondsShift = 1;
    }

    String timeString = getCurrentTimeString(secondsShift);
    String filename = timeString + "." + type;
    try {
      File statusFile = Paths.get(getUnsharedStoriesPath(), filename).toFile();
      if (contents == null) {
        statusFile.createNewFile();
      } else {
        fileUtils.writePrototxt(contents, statusFile.toString());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    if (type != FileData.Type.RUNNING) {
      System.out.println(String.format("\n%s tracking: %s", type, timeString));
    }
  }

  private String getCurrentTimeString(int secondsShift) {
    long currentTime = System.currentTimeMillis() + secondsShift * 1000;
    return DATE_FORMATTER.format(new Date(currentTime));
  }

  private String getCurrentTimeString() {
    return getCurrentTimeString(0);
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
      fileUtils.writePrototxt(
          ScreenshotMetadata.newBuilder().setProject(project).setOneliner(story).build(),
          filenameWithoutExtension + ".prototxt");
    } catch (AWTException | IOException e) {
      log.error("Error in saving screenshot", e);
    }
  }
}
