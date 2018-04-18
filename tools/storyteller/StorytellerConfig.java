package tools.storyteller;

import com.google.startupos.common.FileUtils;
import com.google.startupos.common.Logger;
import com.google.startupos.common.flags.Flags;
import java.util.Arrays;
import tools.storyteller.Protos.Config;

/*
 * Config for Storyteller
 */
public class StorytellerConfig {
  private static final Logger log = Logger.getForClass();
  private static final String CONFIG_FILE = "~/.storyteller";
  private static final String DEFAULT_STORIES_PATH = "/base/local/storyteller";
  private static final String INVOICES_PATH = "/base/local/storyteller";

  public static Config getConfig() {
    Config.Builder config =
        Config.newBuilder()
                .setStoriesPath(DEFAULT_STORIES_PATH)
                .setInvoicesPath(INVOICES_PATH)
                .addAllProjects(Arrays.asList("Front-end", "Backend", "Fixing issue", "Code review", "Production"));
    if (!FileUtils.fileExists(CONFIG_FILE)) {
      log.debug("No config found in " + CONFIG_FILE);
      return config.build();
    }
    return (Config) FileUtils.readPrototxtUnchecked(CONFIG_FILE, config);
  }
}

