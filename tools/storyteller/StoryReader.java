package tools.storyteller;

import com.google.common.collect.ImmutableList;
import com.google.startupos.common.FileUtils;
import com.google.startupos.common.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;
import tools.storyteller.service.Protos.Story;
import tools.storyteller.service.Protos.StoryList;

/* Storyteller reader that reads story files into a Story proto. */
@Singleton
public class StoryReader {
  private static final Logger log = Logger.getForClass();

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
