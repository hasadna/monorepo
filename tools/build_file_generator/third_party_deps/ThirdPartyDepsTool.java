package tools.build_file_generator.third_party_deps;

import com.google.startupos.common.CommonModule;
import com.google.startupos.common.FileUtils;
import com.google.startupos.common.flags.Flag;
import com.google.startupos.common.flags.FlagDesc;
import com.google.startupos.common.flags.Flags;
import com.google.startupos.tools.build_file_generator.third_party_deps.ThirdPartyDepsAnalyzer;
import dagger.Component;

import java.io.IOException;
import javax.inject.Singleton;

/* A tool for generating the zip-archive with prototxt file inside
 * to know which classes each third_party dependency includes.
 * Path to the zip-archive: "<project_root>/third_party_deps.zip"
 *
 * Usage:
 * bazel run //tools/build_file_generator/third_party_deps:third_party_deps_tool --
 * --build_file_path <path/to/BUILD/file>
 */
public class ThirdPartyDepsTool {

  private static final String PATH_TO_ZIP = "third_party_deps.zip";
  private static final String PROTOTXT_FILENAME_INSIDE_ZIP = "third_party_deps.prototxt";

  @FlagDesc(
      name = "build_file_path",
      description =
          "Path to BUILD file with third party deps inside the project. "
              + "The file usually located by path: "
              + "tools/build_file_generator/third_party_deps/BUILD")
  private static Flag<String> buildFilePath = Flag.create("");

  public static void main(String[] args) throws IOException {
    Flags.parseCurrentPackage(args);
    ThirdPartyDepsToolComponent component =
        DaggerThirdPartyDepsTool_ThirdPartyDepsToolComponent.create();

    FileUtils fileUtils = component.getFileUtils();
    fileUtils.writePrototxtToZip(
        component
            .getThirdPartyDepsAnalyzer()
            .getThirdPartyDeps(
                fileUtils.joinPaths(fileUtils.getCurrentWorkingDirectory(), buildFilePath.get())),
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

