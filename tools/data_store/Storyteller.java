package tools.storyteller;

// import com.google.common.collect.ImmutableList;
// import com.google.common.collect.Iterables;
// import com.google.common.flogger.FluentLogger;
// import com.google.startupos.common.FileUtils;
// import com.google.startupos.common.StringBuilder;
// import com.google.startupos.common.Strings;
// import com.google.startupos.common.Time;
// import com.google.startupos.common.firestore.FirestoreProtoClient;
// import com.google.startupos.tools.reviewer.local_server.service.AuthService;
// import java.io.IOException;
// import java.nio.file.Paths;
// import java.time.Instant;
// import java.time.LocalDateTime;
// import java.time.YearMonth;
// import java.time.ZoneId;
import javax.inject.Inject;
// import tools.storyteller.Protos.Config;
// import tools.storyteller.Protos.Screenshot;
// import tools.storyteller.Protos.Story;
// import tools.storyteller.Protos.StoryItem;
// import tools.storyteller.Protos.StoryList;

/*
 * Storyteller logic.
 *
 * For an introduction to Storyteller, see StorytellerTool.
 */
public class Storyteller {
  // private static final FluentLogger log = FluentLogger.forEnclosingClass();
  // // Output a RUNNING file every X minutes.
  // private static final int UPDATE_RUNNING_STATUS_MINUTES = 5;
  // // Number of most recent shared stories to output
  // private static final int RECENT_SHARED_STORIES_COUNT = 10;

  // private static final String FIRESTORE_SCREENSHOT_COLLECTION =
  //     "storyteller/data/user/%s/screenshot";
  // private static final String FIRESTORE_STORIES_COLLECTION = "storyteller/data/user/%s/story";

  // // User's email
  // private final String author;
  // private Config config;
  // private int screenshotFrequency;
  // private StoryReader reader;
  // private StoryWriter writer;
  // private FileUtils fileUtils;
  // private FirestoreProtoClient firestoreClient;

  @Inject
  public Storyteller(
      // StorytellerConfig storytellerConfig,
      // StoryReader reader,
      // StoryWriter writer,
      // FileUtils fileUtils,
      // AuthService authService) {
  ) {
    // config = storytellerConfig.getConfig();
    // this.reader = reader;
    // this.writer = writer;
    // this.fileUtils = fileUtils;
    // screenshot`/Frequency = getScreenshotFrequency();
    // load file from ~/Desktop/serviceAccount.json
    // firestoreClient = new FirestoreProtoClient(authService.getProjectId(), authService.getToken());
    // author = authService.getUserEmail();
  }

}

