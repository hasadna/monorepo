package tools.storyteller;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import com.google.startupos.common.FileUtils;
import com.google.startupos.common.Logger;
import tools.storyteller.Protos.FileData;
import tools.storyteller.Protos.StatusData;
import tools.storyteller.service.Protos.Story;
import tools.storyteller.service.Protos.StoryItem;
import tools.storyteller.Protos.ScreenshotMetadata;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Optional;

/* Storyteller reader that reads story files into a Story proto. */
@Singleton
public class StoryReader {
  private static final Logger log = Logger.getForClass();

  private FileUtils fileUtils;

  @Inject
  public StoryReader(FileUtils fileUtils) {
    this.fileUtils = fileUtils;
  }

  private ImmutableList<FileData> getFileData(String path) throws ParseException {
    ImmutableList.Builder<FileData> result = new ImmutableList.Builder<>();
    if (!Paths.get(path).toFile().exists()) {
      return result.build();
    }
    // TODO: Figure out if there's a better way to get sorted filenames:
    ImmutableList<String> filenames =
        ImmutableList.sortedCopyOf(Arrays.asList(Paths.get(path).toFile().list()));
    for (String filename : filenames) {
      String[] parts = filename.split("\\.");
      checkState(
          parts.length == 2,
          "Filename %s has incorrect format. Should be <date>.<extension>",
          filename);
      String dateString = parts[0];
      long timeMs = StoryWriter.DATE_FORMATTER.parse(dateString).getTime();
      String extension = parts[1];
      FileData.Type type = FileData.Type.UNKNOWN;
      FileData.Builder fileData = FileData.newBuilder();
      if (extension.equals("jpg")) {
        type = FileData.Type.SCREENSHOT;
      } else if (extension.equals("prototxt")) {
        type = FileData.Type.SCREENSHOT_METADATA;
        ScreenshotMetadata metadata =
            (ScreenshotMetadata)
                fileUtils.readPrototxtUnchecked(
                    Paths.get(path, filename).toString(), ScreenshotMetadata.newBuilder());
        fileData.setScreenshotMetadata(metadata);
      } else {
        type = FileData.Type.valueOf(extension);
        StatusData statusData =
            (StatusData)
                fileUtils.readPrototxtUnchecked(
                    Paths.get(path, filename).toString(), StatusData.newBuilder());
        fileData.setStatusData(statusData);
      }
      result.add(fileData.setType(type).setTimeMs(timeMs).setFilename(filename).build());
    }
    return result.build();
  }

  public static Optional<FileData> findFileData(
      ImmutableList<FileData> list, FileData.Type type, long timeMs) {
    return list.stream().filter(x -> x.getType() == type && x.getTimeMs() == timeMs).findFirst();
  }

  private ImmutableList<Story> toStories(ImmutableList<FileData> fileDataList) {
    StoryVerifier.checkFiles(fileDataList);
    ImmutableList.Builder<Story> result = new ImmutableList.Builder<>();
    Story.Builder currentStory = Story.newBuilder();
    for (FileData fileData : fileDataList) {
      if (fileData.getType() == FileData.Type.START) {
        currentStory.setStartTimeMs(fileData.getTimeMs());
        currentStory.setProject(fileData.getStatusData().getProject());
      } else if (fileData.getType() == FileData.Type.END) {
        String project = fileData.getStatusData().getProject();
        if (!project.equals(currentStory.getProject())) {
          throw new IllegalStateException(
              String.format(
                  "Mismatch between current project %s and END file:\n%s",
                  currentStory.getProject(), fileData.toString()));
        }
        currentStory.setEndTimeMs(fileData.getTimeMs());
        result.add(currentStory.build());
        currentStory = Story.newBuilder().setStartTimeMs(fileData.getTimeMs());
      } else if (fileData.getType() == FileData.Type.RUNNING) {
        String project = fileData.getStatusData().getProject();
        if (!project.equals(currentStory.getProject())) {
          currentStory.setEndTimeMs(fileData.getTimeMs());
          result.add(currentStory.build());
          currentStory =
              Story.newBuilder().setProject(project).setStartTimeMs(fileData.getTimeMs());
        }
      } else if (fileData.getType() == FileData.Type.SCREENSHOT) {
        FileData metadataFileData =
            findFileData(fileDataList, FileData.Type.SCREENSHOT_METADATA, fileData.getTimeMs())
                .get();
        ScreenshotMetadata metadata = metadataFileData.getScreenshotMetadata();
        String project = metadata.getProject();
        if (currentStory.getProject().isEmpty()) {
          currentStory.setProject(project);
        } else if (!currentStory.getProject().equals(project)) {
          currentStory.setEndTimeMs(fileData.getTimeMs());
          result.add(currentStory.build());
          currentStory =
              Story.newBuilder().setProject(project).setStartTimeMs(fileData.getTimeMs());
        }
        currentStory.addItem(
            StoryItem.newBuilder()
                .setTimeMs(fileData.getTimeMs())
                .setOneliner(metadata.getOneliner())
                .build());
      } else if (fileData.getType() == FileData.Type.SCREENSHOT_METADATA) {
        // Do nothing. We already read the data in Type.SCREENSHOT.
      } else {
        throw new IllegalStateException(
            String.format("Unknown file type %s for file:\n%s", fileData.getType(), fileData));
      }
    }
    StoryVerifier.checkStories(result.build(), false);
    return result.build();
  }

  public ImmutableList<Story> getStories(String path) throws ParseException {
    return toStories(getFileData(path));
  }
}
