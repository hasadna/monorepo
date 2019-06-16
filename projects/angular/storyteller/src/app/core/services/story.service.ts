import { Injectable } from '@angular/core';

import { StoryList, Story, ReviewerConfig, User } from '@/core/proto';
import { EasyStory } from '@/shared';

@Injectable()
export class StoryService {
  // Converts list of lists to single list
  getStories(storyLists: StoryList[]): Story[] {
    const stories: Story[] = [];
    for (const storyList of storyLists) {
      stories.push(...storyList.getStoryList());
    }
    return stories;
  }

  // Converts Story[] and Screenshot[] to EasyStory[]
  createEasyStories(stories: Story[], user: User): EasyStory[] {
    const easyStories: EasyStory[] = [];
    for (const story of stories) {
      easyStories.push(...this.createEasyStoryItemList(story, user));
    }
    return easyStories;
  }

  // Converts Story and Screenshot[] to EasyStory[]
  createEasyStoryItemList(story: Story, user: User): EasyStory[] {
    const easyStoryBuilder: EasyStory = {
      storyId: story.getId(),
      itemId: '',
      username: user.getFirstName() + ' ' + user.getLastName(),
      imageURL: user.getImageUrl(),
      email: user.getEmail(),
      project: story.getProject(),
      timestamp: 0,
      oneliner: '',
      note: '',
      screenshotName: '',
    };

    const easyStories: EasyStory[] = [];
    story.getItemList().forEach(storyItem => {
      const easyStory: EasyStory = Object.assign({}, easyStoryBuilder);
      easyStory.itemId = storyItem.getId();
      easyStory.timestamp = storyItem.getTimeMs();
      easyStory.oneliner = storyItem.getOneliner();
      easyStory.note = storyItem.getNote();
      easyStory.screenshotName = storyItem.getScreenshotFilename();
      easyStories.push(easyStory);
    });
    return easyStories;
  }

  getProjectIdList(reviewerConfig: ReviewerConfig): string[] {
    return reviewerConfig.getProjectList().map(
      project => project.getId(),
    );
  }

  getUser(email: string, reviewerConfig: ReviewerConfig): User {
    for (const user of reviewerConfig.getUserList()) {
      if (user.getEmail() === email) {
        return user;
      }
    }
  }

  sortStoriesByTime(easyStories: EasyStory[]): void {
    // Newest first
    easyStories.sort((a, b) => {
      return Math.sign(b.timestamp - a.timestamp);
    });
  }
}
