package tools.storyteller;

// import com.google.common.collect.ImmutableList;
// import com.google.common.flogger.FluentLogger;
// import com.google.startupos.common.CommonModule;
// import com.google.startupos.common.flags.Flags;
// import com.google.startupos.tools.reviewer.aa.AaModule;
// import com.google.startupos.tools.reviewer.local_server.service.AuthService;
import dagger.Component;
import javax.inject.Singleton;

/*
 */
public class StorytellerTool {


  public String downloadFrom(String bucketName, String fileName) throws IOException {
    String[] parts = fileName.split("[.]");
    String name = parts[0];
    String extension = ".tmp";
    if (parts.length > 1) {
      extension = "." + parts[parts.length - 1];
    }
    File tempFile = File.createTempFile(name, extension);
    storage.get(BlobId.of(bucketName, fileName)).downloadTo(Paths.get(tempFile.getAbsolutePath()));
    return tempFile.getAbsolutePath();
  }
  public void loadFile(File file)
  {
    

  }

  // private static final FluentLogger log = FluentLogger.forEnclosingClass();
  // private static final String HELP = "help";
  // private static final String LIST = "list";
  // private static final String SHARE = "share";
  // private static final String INVOICE = "invoice";
  // private static final ImmutableList<String> COMMANDS =
  //     ImmutableList.of(HELP, LIST, SHARE, INVOICE);

  @Singleton
  // @Component(modules = {CommonModule.class, AaModule.class})
  @Component
  interface StorytellerToolComponent {
    Storyteller getStoryteller();
  }

  public static void main(String[] args) throws Exception {
    System.out.println("Hello World");
    
    // String[] leftoverArgs =
    //     Flags.parse(args, StorytellerTool.class.getPackage(), AuthService.class.getPackage());
    Storyteller storyteller =
        DaggerStorytellerTool_StorytellerToolComponent.create().getStoryteller();

    // String option = leftoverArgs.length > 0 ? leftoverArgs[0] : null;

    // if (HELP.equals(option)) {
    //   System.out.println("Commands are: " + String.join(", ", COMMANDS));
    // } else if (LIST.equals(option)) {
    //   storyteller.list();
    // } else if (SHARE.equals(option)) {
    //   storyteller.share();
    // } else if (INVOICE.equals(option)) {
    //   storyteller.invoice();
    // } else {
    //   System.out.println("Unknown command. Commands are: " + String.join(", ", COMMANDS));
    // }
  }
}

