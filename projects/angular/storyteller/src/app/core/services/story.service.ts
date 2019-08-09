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
      timestamp: moment.getTimestampMs(),
      oneliner: story.getOneliner(),
      note: moment.getNote(),
      screenshotName: moment.getScreenshot(),
    };
    return easyStory;
  }

  getUser(email: string, reviewerConfig: ReviewerConfig): User {
    for (const user of reviewerConfig.getUserList()) {
      if (user.getEmail() === email) {
        return user;
      }
    }

    // User isn't listed in ReviewerConfig.
    // Mock user is used instead
    const mockUser = new User();
    mockUser.setEmail(email);
    mockUser.setFirstName(email.split('@')[0]);
    mockUser.setImageUrl('/assets/mock-avatar.svg');
    return mockUser;
  }

  sortStoriesByTime(easyStories: EasyStory[]): void {
    // Newest first
    easyStories.sort((a, b) => {
      return Math.sign(b.timestamp - a.timestamp);
    });
  }
}
