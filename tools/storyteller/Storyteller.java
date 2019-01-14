package tools.storyteller;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.flogger.FluentLogger;
import com.google.startupos.common.FileUtils;
import com.google.startupos.common.StringBuilder;
import com.google.startupos.common.Strings;
import com.google.startupos.common.Time;
import com.google.startupos.common.firestore.FirestoreProtoClient;
import com.google.startupos.tools.localserver.service.AuthService;
import tools.storyteller.Protos.Config;
import tools.storyteller.Protos.Story;
import tools.storyteller.Protos.StoryItem;
import tools.storyteller.Protos.StoryList;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;

/*
 * Storyteller logic.
 *
 * For an introduction to Storyteller, see StorytellerTool.
 */
public class Storyteller {
  private static final FluentLogger log = FluentLogger.forEnclosingClass();
  // Output a RUNNING file every X minutes.
  private static final int UPDATE_RUNNING_STATUS_MINUTES = 5;
  // Number of most recent shared stories to output
  private static final int RECENT_SHARED_STORIES_COUNT = 10;

  private static final String FIRESTORE_STORYTELLER_ROOT = "/storyteller";

  private Config config;
  private int screenshotFrequency;
  private StoryReader reader;
  private StoryWriter writer;
  private FileUtils fileUtils;
  private FirestoreProtoClient firestoreClient;

  @Inject
  public Storyteller(
      StorytellerConfig storytellerConfig,
      StoryReader reader,
      StoryWriter writer,
      FileUtils fileUtils,
      AuthService authService) {
    config = storytellerConfig.getConfig();
    this.reader = reader;
    this.writer = writer;
    this.fileUtils = fileUtils;
    screenshotFrequency = getScreenshotFrequency();
    firestoreClient = new FirestoreProtoClient(authService.getProjectId(), authService.getToken());
  }

  /*
   * Run storyteller forever.
   *
   * This means writing periodic RUNNING files and screenshots.
   */
  public void runForever() throws Exception {
    int passedMinutes = 0;
    System.out.println();
    while (true) {
      Thread.sleep(1000 * 60);
      passedMinutes += 1;
      periodicUpdate(passedMinutes, "", "");
    }
  }

  /* Periodic update should be called every minute.*/
  public void periodicUpdate(int passedMinutes, String project, String story) {
    // Print '.' every minute, 'R' every 10 minutes and 'H' every hour.
    if (passedMinutes % 60 == 0) {
      System.out.print("H");
    } else if (passedMinutes % screenshotFrequency == 0) {
      System.out.print("S");
      writer.saveScreenshot();
      writer.saveStoryItem(project, story);
    } else if (passedMinutes % UPDATE_RUNNING_STATUS_MINUTES == 0) {
      System.out.print("R");
      writer.updateStory(project);
    } else {
      System.out.print(".");
    }
  }

  public static String getSharedStoriesPath(Config config) {
    return Paths.get(config.getStoriesPath(), "shared").toString();
  }

  public static String getUnsharedStoriesPath(Config config) {
    return Paths.get(config.getStoriesPath(), "unshared").toString();
  }

  private String getSharedStoriesPath() {
    return getSharedStoriesPath(config);
  }

  private String getUnsharedStoriesPath() {
    return getUnsharedStoriesPath(config);
  }

  /*
   * Share storyteller stories with everyone.
   *
   * This method shares stories to Firebase and moves them to the shared folder.
   */
  public void share() {
    StoryList storyList = StoryList.newBuilder().addAllStory(getUnsharedStories()).build();
    firestoreClient.addProtoDocumentToCollection(FIRESTORE_STORYTELLER_ROOT, storyList);

    try {
      writer.saveSharedStories(storyList);
      System.out.println(storyList.getStoryCount() + " stories shared");
      fileUtils.clearDirectory(getUnsharedStoriesPath());
      System.out.println("Folder with unshared stories is cleared.");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String storiesToString(ImmutableList<Story> stories) {
    StringBuilder sb = new StringBuilder();
    for (Story story : stories) {
      sb.appendln(
          "A story about '%s' from %s (%s):",
          story.getProject(),
          Time.getDateString(story.getStartTimeMs()),
          Time.getHourMinuteDurationString(story.getEndTimeMs() - story.getStartTimeMs()));
      for (StoryItem item : story.getItemList()) {
        sb.appendln(
            "%s: %s",
            Time.getHourMinuteString(item.getTimeMs()),
            Strings.capitalize(item.getOneliner().replace("_", " ")));
      }
      sb.appendln();
    }
    return sb.toString();
  }

  /* Prints a summary of the unshared and recently shared stories. */
  public void list() {
    StringBuilder sb = new StringBuilder();
    sb.appendln();
    sb.appendln("*** Recent stories: ***");
    sb.appendln("=======================");
    ImmutableList<Story> stories = reader.getStories(getSharedStoriesPath());
    sb.appendln(
        storiesToString(
            stories.subList(
                Math.max(stories.size() - RECENT_SHARED_STORIES_COUNT, 0), stories.size())));

    sb.appendln("*** Unshared stories: ***");
    sb.appendln("=========================");
    sb.appendln(
        storiesToString(
            reader.getStories(
                fileUtils.joinPaths(
                    getUnsharedStoriesPath(), StorytellerConfig.STORIES_FILENAME))));
    System.out.print(sb);
  }

  /* Saves an invoice. */
  public void invoice() throws Exception {
    ImmutableList<Story> stories = reader.getStories(getSharedStoriesPath());
    Instant timeOfIssue = Instant.now();
    long invoiceNumber = timeOfIssue.getEpochSecond();
    YearMonth lastMonth = getLastMonth(timeOfIssue);
    long startTime = Time.getMillis(lastMonth.atDay(1));
    // For sime reason, Time.format formats endTimeForFilter on the next day,
    //     so we separate the exact time as endTimeForFilter and the
    //     not-exact-but-formats-correctly as endTime
    long endTimeForFilter = Time.getMillis(lastMonth.atEndOfMonth().plusDays(1)) - 1;
    long endTime = Time.getMillis(lastMonth.atEndOfMonth());

    // Filter stories in time range according to their start time
    stories =
        ImmutableList.copyOf(
            Iterables.filter(
                stories,
                story ->
                    startTime < story.getStartTimeMs()
                        && story.getStartTimeMs() <= endTimeForFilter));

    Invoicer.saveInvoice(
        Paths.get(config.getInvoicesPath(), invoiceNumber + ".pdf").toString(),
        config.getInvoiceCreator(),
        config.getInvoiceReceiver(),
        stories,
        startTime,
        endTime,
        invoiceNumber,
        timeOfIssue.toEpochMilli());
  }

  YearMonth getLastMonth(Instant instant) {
    LocalDateTime time = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
    YearMonth yearMonth = YearMonth.of(time.getYear(), time.getMonth());
    return yearMonth.minusMonths(1);
  }

  public void startup(String project) {
    writer.startStory(project);
  }

  public void shutdown(String project) {
    writer.endStory(project);
  }

  public void saveScreenshot() {
    writer.saveScreenshot();
  }

  public ImmutableList<Story> getUnsharedStories() {
    return reader.getStories(
        fileUtils.joinPaths(getUnsharedStoriesPath(), StorytellerConfig.STORIES_FILENAME));
  }

  private int getScreenshotFrequency() {
    switch (config.getScreenshotFrequency()) {
      case EVERY_10_MINUTES:
        return 10;
      case EVERY_15_MINUTES:
        return 15;
      case EVERY_20_MINUTES:
        return 20;
      case UNRECOGNIZED:
        throw new IllegalStateException(
            "Unknown screenshot frequency " + config.getScreenshotFrequency());
    }
    return -1; // We should never get here
  }
}

