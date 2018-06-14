package tools.storyteller;

import com.google.startupos.common.FileUtils;
import com.google.common.flogger.FluentLogger;
import java.util.Arrays;
import tools.storyteller.Protos.Config;
import javax.inject.Inject;
import javax.inject.Singleton;

/*
 * Config for Storyteller
 */
@Singleton
public class StorytellerConfig {
  private static final FluentLogger log = FluentLogger.forEnclosingClass();
  private static final String CONFIG_FILE = "~/.storyteller";
  private static final String DEFAULT_STORIES_PATH = "/base/local/storyteller";
  private static final String INVOICES_PATH = "/base/local/storyteller";
  static final String STORIES_FILENAME = "stories.prototxt";

  private FileUtils fileUtils;

  @Inject
  public StorytellerConfig(FileUtils fileUtils){
    this.fileUtils = fileUtils;
  }

  public Config getConfig() {
    Config.Builder config =
        Config.newBuilder()
                .setStoriesPath(DEFAULT_STORIES_PATH)
                .setInvoicesPath(INVOICES_PATH)
                .addAllProjects(Arrays.asList("Front-end", "Backend", "Fixing issue", "Code review", "Production"));
    if (!fileUtils.fileExists(CONFIG_FILE)) {
      log.atFine().log("No config found in %s", CONFIG_FILE);
      return config.build();
    }
    return (Config) fileUtils.readPrototxtUnchecked(CONFIG_FILE, config);
  }
}
