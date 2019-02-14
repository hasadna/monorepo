package tools.storyteller;

import com.google.common.collect.ImmutableList;
import com.google.common.flogger.FluentLogger;
import com.google.startupos.common.CommonModule;
import com.google.startupos.common.flags.Flags;
import com.google.startupos.tools.reviewer.aa.AaModule;
import com.google.startupos.tools.reviewer.local_server.service.AuthService;
import dagger.Component;
import javax.inject.Singleton;

/*
 * A way to tell the story of what you're up to.
 *
 * Storyteller is a team communication tool. Its purpose is to help you tell people what you're
 * working on, how you're working on it, the problems you encountered on the way that you bravely
 * and creatively solved, as well as funny, sad and amazing things that happened along the way.
 * You can do this by running it, taking screenshots (AKA "desktop selfies"), and later choosing
 * which to share.
 *
 * How Storyteller works:
 * - Storyteller is a small desktop app. It has 2 modes:
 *   - Time tracking and screenshot
 *   - Screenshot only
 * - All of storyteller's output files are stored in a folder, configurable using stories_path.
 * - You can review all output files (screenshots, stories, time tracking if enabled).
 * - When you're done, you run 'storyteller_tool share' to upload the files and share them. Stories
 *   are shared with your whole team. This way, everyone can know what you're up to,
 *   ask you questions, offer help, quench their curiosity, etc.
 *
 * - Note: If you work hourly, the time tracking part can be used to create invoice pdfs. Note that
 *     some countries may have additional requirements such as digital signatures, which we don't
 *     currently support.
 *
 * Configuration:
 * Edit and copy example_config.txt to ~/.storyteller
 *
 * Usage:
 * To build:
 * bazel build //tools/storyteller:storyteller_tool;
 * To list stories:
 * bazel-bin/tools/storyteller/storyteller_tool list
 * To share stories:
 * bazel-bin/tools/storyteller/storyteller_tool share
 * To run desktop app in screenshot-only mode:
 * bazel run //tools/storyteller:storyteller_screenshot_ui_tool
 * To run desktop app in time-and-screenshot mode:
 * bazel run //tools/storyteller:storyteller_time_and_screenshot_ui_tool
 */
public class StorytellerTool {

  private static final FluentLogger log = FluentLogger.forEnclosingClass();
  private static final String HELP = "help";
  private static final String LIST = "list";
  private static final String SHARE = "share";
  private static final String INVOICE = "invoice";
  private static final ImmutableList<String> COMMANDS =
      ImmutableList.of(HELP, LIST, SHARE, INVOICE);

  @Singleton
  @Component(modules = {CommonModule.class, AaModule.class})
  interface StorytellerToolComponent {
    Storyteller getStoryteller();
  }

  public static void main(String[] args) throws Exception {
    String[] leftoverArgs =
        Flags.parse(args, StorytellerTool.class.getPackage(), AuthService.class.getPackage());
    Storyteller storyteller =
        DaggerStorytellerTool_StorytellerToolComponent.create().getStoryteller();

    String option = leftoverArgs.length > 0 ? leftoverArgs[0] : null;

    if (HELP.equals(option)) {
      System.out.println("Commands are: " + String.join(", ", COMMANDS));
    } else if (LIST.equals(option)) {
      storyteller.list();
    } else if (SHARE.equals(option)) {
      storyteller.share();
    } else if (INVOICE.equals(option)) {
      storyteller.invoice();
    } else {
      System.out.println("Unknown command. Commands are: " + String.join(", ", COMMANDS));
    }
  }
}

