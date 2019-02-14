package hasadna.projects.noloan2.server;

import com.google.startupos.common.CommonModule;
import com.google.startupos.common.FileUtils;
import com.google.startupos.common.flags.Flag;
import com.google.startupos.common.flags.FlagDesc;
import com.google.startupos.common.flags.Flags;
import dagger.Component;
import java.io.IOException;
import java.util.Base64;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DataUploader {

  private FileUtils fileUtils;

  @Inject
  DataUploader(FileUtils fileUtils) {
    this.fileUtils = fileUtils;
  }

  void run() throws IOException {
    // TODO Implement
  }

  @Singleton
  @Component(modules = CommonModule.class)
  interface ToolComponent {
    DataUploader getTool();
  }

  public static void main(String[] args) throws IOException {
    Flags.parseCurrentPackage(args);
    DaggerDataUploader_ToolComponent.create().getTool().run();
  }
}

