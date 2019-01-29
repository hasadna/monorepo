package tools.storyteller;

import com.google.common.flogger.FluentLogger;
import com.google.startupos.common.FileUtils;
import java.util.Arrays;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import tools.storyteller.Protos.Config;

/*
 * Config for Storyteller
 */
@Singleton
public class StorytellerConfig {
  private static final FluentLogger log = FluentLogger.forEnclosingClass();
  private static final String CONFIG_FILE = "~/.storyteller";
  static final String STORIES_FILENAME = "stories.prototxt";

  private FileUtils fileUtils;
  private String storytellerPath;

  @Inject
  public StorytellerConfig(FileUtils fileUtils, @Named("Base path") String basePath) {
    this.fileUtils = fileUtils;
    this.storytellerPath = fileUtils.joinPaths(basePath, "local", "storyteller");
  }

  public Config getConfig() {
    Config.Builder config =
        Config.newBuilder()
            .setStoriesPath(storytellerPath)
            .setInvoicesPath(storytellerPath)
            .addAllProjects(
                Arrays.asList("Front-end", "Backend", "Fixing issue", "Code review", "Production"));
    if (!fileUtils.fileExists(CONFIG_FILE)) {
      log.atFine().log("No config found in %s", CONFIG_FILE);
      return config.build();
    }
    return (Config) fileUtils.readPrototxtUnchecked(CONFIG_FILE, config);
  }
}

