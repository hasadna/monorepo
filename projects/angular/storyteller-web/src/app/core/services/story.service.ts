import { Injectable } from '@angular/core';
import { Observable, zip } from 'rxjs';

import { Story, Moment, ReviewerConfig, User } from '@/core/proto';
import { EasyStory } from '@/core/interfaces';
import { FirebaseService } from './firebase.service';

@Injectable()
export class StoryService {
  constructor(private firebaseService: FirebaseService) { }

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

  // Loads Story[] and Moment[] of specific user and converts them to EasyStory[]
  getUserEasyStories(user: User): Observable<EasyStory[]> {
    return new Observable(observer => {
      // Load all stories of the user
      this.firebaseService.getStoryList(user.getEmail()).subscribe(storyList => {
        // Load packs of moments for each Story
        if (storyList.length === 0) {
          observer.next([]);
          return;
        }
        const observables: Observable<Moment[]>[] = [];
        for (const story of storyList) {
          observables.push(this.firebaseService.getMoments(user.getEmail(), story.getId()));
        }
        zip(...observables).subscribe(data => {
          // Create EasyStory for each Moment
          const easyStories: EasyStory[] = [];
          for (const i in storyList) {
            const story: Story = storyList[i];
            const moments: Moment[] = data[i];
            for (const moment of moments) {
              easyStories.push(this.createEasyStory(story, moment, user));
            }
          }
          this.sortStoriesByTime(easyStories);
          observer.next(easyStories);
        });
      });
    });
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
