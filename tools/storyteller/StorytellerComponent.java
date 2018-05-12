package tools.storyteller;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component(modules = { StoryReader.class, StoryWriter.class })
public interface StorytellerComponent {
  Storyteller getStoryteller();
  StorytellerConfig getStorytellerConfig();
}
