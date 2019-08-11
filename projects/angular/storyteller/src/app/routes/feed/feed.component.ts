import { Component, OnDestroy } from '@angular/core';
import { Observable, zip, Subscription } from 'rxjs';

import { User } from '@/core/proto';
import { EasyStory } from '@/core/interfaces';
import {
  StoryService,
  LoadingService,
  FirestoryService,
  AuthService,
} from '@/core/services';

@Component({
  selector: 'page-feed',
  templateUrl: './feed.component.html',
})
export class FeedComponent implements OnDestroy {
  easyStories: EasyStory[];
  configSub = new Subscription();
  zipSub = new Subscription();

  constructor(
    private storyService: StoryService,
    public loadingService: LoadingService,
    private firestoryService: FirestoryService,
    private authService: AuthService,
  ) {
    this.loadingService.isLoading = true;
    this.loadReviewerConfig();
  }

  // Loads all available stories
  loadStories(users: User[]): void {
    const observableRequests: Observable<EasyStory[]>[] = users.map(user => {
      return this.firestoryService.getUserEasyStories(user);
    });
    this.zipSub = zip(...observableRequests).subscribe(easyStoriesBundle => {
      const easyStories: EasyStory[] = [];
      for (const easyStoriesGroup of easyStoriesBundle) {
        easyStories.push(...easyStoriesGroup);
      }
      this.storyService.sortStoriesByTime(easyStories);
      this.easyStories = easyStories;
      this.loadingService.isLoading = false;
    });
  }

  loadReviewerConfig(): void {
    this.configSub = this.authService.onloadReviewerConfig().subscribe(reviewerConfig => {
      this.loadStories(reviewerConfig.getUserList());
    });
  }

  ngOnDestroy() {
    this.configSub.unsubscribe();
    this.zipSub.unsubscribe();
    this.firestoryService.unsubscribe();
  }
}
