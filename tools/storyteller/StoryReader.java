package tools.storyteller;

import com.google.common.collect.ImmutableList;
import com.google.common.flogger.FluentLogger;
import com.google.startupos.common.FileUtils;
import javax.inject.Inject;
import javax.inject.Singleton;
import tools.storyteller.Protos.Story;
import tools.storyteller.Protos.StoryList;


/* Storyteller reader that reads story files into a Story proto. */
@Singleton
public class StoryReader {
  private static final FluentLogger log = FluentLogger.forEnclosingClass();

  private FileUtils fileUtils;

  @Inject
  public StoryReader(FileUtils fileUtils) {
    this.fileUtils = fileUtils;
  }

  public ImmutableList<Story> getStories(String path) {
    if (fileUtils.fileExists(path)) {
      StoryList stories = (StoryList) fileUtils.readPrototxtUnchecked(path, StoryList.newBuilder());
      return ImmutableList.copyOf(stories.getStoryList());
    } else {
      return ImmutableList.of();
    }
  }
}

