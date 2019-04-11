import { Injectable } from '@angular/core';

import { Screenshot, StoryList, Story, ReviewerConfig, User, StoryItem } from '@/core/proto';
import { EasyStory } from '@/shared';

@Injectable()
export class StoryService {
  // Converts list of lists to single list
  getStories(storyLists: StoryList[]): Story[] {
    // TODO: find out why we need StoryList.
    // It's list of lists. Why we can't have just single list?

    // Moreover each story contains list of story items.
    // So it's list of lists of lists... It's a madness!

    const stories: Story[] = [];
    for (const storyList of storyLists) {
      stories.push(...storyList.getStoryList());
    }

    return stories;
  }

  // Gets screenshots, which are part of the stories
  filterScreenshots(screenshots: Screenshot[], stories: Story[]): Screenshot[] {
    const filteredScreenshots: Screenshot[] = screenshots.filter(screenshot => {
      for (const story of stories) {
        for (const storyItem of story.getItemList()) {
          if (screenshot.getFilename() === storyItem.getScreenshotFilename()) {
            return true;
          }
        }
      }
      return false;
    });
    return filteredScreenshots;
  }

  // Converts Story[] and Screenshot[] to EasyStory[]
  createEasyStories(screenshots: Screenshot[], stories: Story[], user: User): EasyStory[] {
    const easyStories: EasyStory[] = [];
    for (const story of stories) {
      easyStories.push(...this.createEasyStoryItemList(screenshots, story, user));
    }
    return easyStories;
  }

  // Converts Story and Screenshot[] to EasyStory[]
  createEasyStoryItemList(screenshots: Screenshot[], story: Story, user: User): EasyStory[] {
    const easyStoryBuilder: EasyStory = {
      storyId: story.getId(),
      itemId: '',
      username: user.getFirstName() + ' ' + user.getLastName(),
      email: user.getEmail(),
      project: story.getProject(),
      timestamp: 0,
      oneliner: '',
      note: '',
      screenshot: '',
    };

    const easyStories: EasyStory[] = [];
    story.getItemList().forEach(storyItem => {
      const easyStory: EasyStory = Object.assign({}, easyStoryBuilder);
      easyStory.itemId = storyItem.getId();
      easyStory.timestamp = storyItem.getTimeMs();
      easyStory.oneliner = storyItem.getOneliner();
      easyStory.note = storyItem.getNote();
      easyStory.screenshot = this.getScreenshot(screenshots, storyItem);
      easyStories.push(easyStory);
    });
    return easyStories;
  }

  getScreenshot(screenshots: Screenshot[], storyItem: StoryItem): string {
    for (const screenshot of screenshots) {
      if (screenshot.getFilename() === storyItem.getScreenshotFilename()) {
        return screenshot.getScreenshot_asB64();
      }
    }
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

  getFilteredStories(easyStories: EasyStory[], projectId: string): EasyStory[] {
    return easyStories.filter(story => {
      return story.project === projectId;
    });
  }
}
