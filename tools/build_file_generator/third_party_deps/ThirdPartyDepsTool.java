package tools.build_file_generator.third_party_deps;

import com.google.startupos.common.CommonModule;
import com.google.startupos.common.FileUtils;
import dagger.Component;

import java.io.IOException;
import javax.inject.Singleton;

/* A tool for generating the zip-archive with prototxt file inside
 * to know which classes each third_party dependency includes.
 * Path to the zip-archive: "<project_root>/third_party_deps.zip"
 *
 * Usage:
 * bazel run //tools/build_file_generator/third_party_deps:third_party_deps_tool
 */
public class ThirdPartyDepsTool {

  private static final String PATH_TO_ZIP = "third_party_deps.zip";
  private static final String PROTOTXT_FILENAME_INSIDE_ZIP = "third_party_deps.prototxt";

  public static void main(String[] args) throws IOException {
    ThirdPartyDepsToolComponent component =
        DaggerThirdPartyDepsTool_ThirdPartyDepsToolComponent.create();

    FileUtils fileUtils = component.getFileUtils();
    fileUtils.writePrototxtToZip(
        component.getThirdPartyDepsAnalyzer().getThirdPartyDeps(),
        fileUtils.joinPaths(fileUtils.getCurrentWorkingDirectory(), PATH_TO_ZIP),
        PROTOTXT_FILENAME_INSIDE_ZIP);
  }

  @Singleton
  @Component(modules = CommonModule.class)
  interface ThirdPartyDepsToolComponent {
    FileUtils getFileUtils();

    ThirdPartyDepsAnalyzer getThirdPartyDepsAnalyzer();
  }
}

