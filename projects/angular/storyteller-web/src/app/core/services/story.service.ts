import { Injectable } from '@angular/core';

import { Story, Moment, ReviewerConfig, User } from '@/core/proto';
import { EasyStory } from '@/core/interfaces';

@Injectable()
export class StoryService {
  // Puts data from different containers to single one
  createEasyStory(story: Story, moment: Moment, user: User): EasyStory {
    const easyStory: EasyStory = {
      storyId: story.getId(),
      momentId: moment.getId(),
      username: (user.getFirstName() + ' ' + user.getLastName()).trim(),
      imageURL: user.getImageUrl(),
      email: user.getEmail(),
      projectId: story.getProjectId(),
      timestamp: moment.getTimestampMs(),
      oneliner: story.getOneliner(),
      note: moment.getNote(),
      screenshotName: moment.getScreenshot(),
    };
    return easyStory;
  }

  // Creates EasyStory[] for several Story[]
  createEasyStories(storyList: Story[], moments: Moment[], user: User): EasyStory[] {
    const easyStories: EasyStory[] = [];
    for (const story of storyList) {
      const storyMoments: Moment[] = moments.filter(
        moment => moment.getStoryId() === story.getId(),
      );
      for (const moment of storyMoments) {
        easyStories.push(this.createEasyStory(story, moment, user));
      }
    }
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
