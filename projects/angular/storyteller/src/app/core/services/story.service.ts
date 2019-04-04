import { Injectable } from '@angular/core';

import { Screenshot, StoryList, Story, ReviewerConfig, User } from '@/core/proto';
import { EasyStory } from '@/shared';

@Injectable()
export class StoryService {
  // Converts list of lists to single list
  getStories(storyLists: StoryList[]): Story[] {
    // TODO: find out why we need StoryList.
    // It's list of lists. Why not just single list?

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
      easyStories.push(this.createEasyStory(screenshots, story, user));
    }
    return easyStories;
  }

  // Converts Story and Screenshot[] to EasyStory
  createEasyStory(screenshots: Screenshot[], story: Story, user: User): EasyStory {
    const easyStory: EasyStory = {
      id: story.getId(),
      username: user.getFirstName() + ' ' + user.getLastName(),
      email: user.getEmail(),
      project: story.getProject(),
      timestamp: story.getEndTimeMs(),
      snapshots: [],
    };

    story.getItemList().forEach(storyItem => {
      for (const screenshot of screenshots) {
        if (screenshot.getFilename() === storyItem.getScreenshotFilename()) {
          easyStory.snapshots.push({
            oneliner: storyItem.getOneliner(),
            note: storyItem.getNote(),
            screenshot: screenshot.getScreenshot_asB64(),
          });
        }
      }
    });

    return easyStory;
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
