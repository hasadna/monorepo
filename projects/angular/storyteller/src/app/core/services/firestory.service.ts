import { Injectable } from '@angular/core';
import { Observable, zip, Subscription } from 'rxjs';

import { Story, Moment, User } from '@/core/proto';
import { EasyStory } from '@/core/interfaces';
import { FirebaseService } from './firebase.service';
import { StoryService } from './story.service';

@Injectable()
export class FirestoryService {
  subs: Subscription[] = [];

  constructor(
    private firebaseService: FirebaseService,
    private storyService: StoryService,
  ) { }

  // Loads Story[] and Moment[] of specific user and converts them to EasyStory[]
  getUserEasyStories(user: User): Observable<EasyStory[]> {
    return new Observable(observer => {
      // Load all stories of the user
      let sub: Subscription;
      sub = this.firebaseService.getStoryList(user.getEmail()).subscribe(storyList => {
        this.subs.push(sub);
        // Load packs of moments for each Story
        if (storyList.length === 0) {
          observer.next([]);
          return;
        }
        const observables: Observable<Moment[]>[] = [];
        for (const story of storyList) {
          observables.push(this.firebaseService.getMoments(user.getEmail(), story.getId()));
        }
        sub = zip(...observables).subscribe(data => {
          this.subs.push(sub);
          // Create EasyStory for each Moment
          const easyStories: EasyStory[] = [];
          for (const i in storyList) {
            const story: Story = storyList[i];
            const moments: Moment[] = data[i];
            for (const moment of moments) {
              easyStories.push(this.storyService.createEasyStory(story, moment, user));
            }
          }
          this.storyService.sortStoriesByTime(easyStories);
          observer.next(easyStories);
        });
      });
    });
  }

  unsubscribe(): void {
    for (const sub of this.subs) {
      sub.unsubscribe();
    }
  }
}
