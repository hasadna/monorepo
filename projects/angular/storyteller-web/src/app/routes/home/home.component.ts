import { Component } from '@angular/core';
import { Observable, zip, Subject } from 'rxjs';

import { User } from '@/core/proto';
import { EasyStory } from '@/core/interfaces';
import { FirebaseService, StoryService, LoadingService } from '@/core/services';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
})
export class HomeComponent {
  projectIdList: string[];
  easyStories: EasyStory[];
  filterChanges = new Subject<string>();

  constructor(
    private firebaseService: FirebaseService,
    private storyService: StoryService,
    public loadingService: LoadingService,
  ) {
    this.loadingService.load();
    this.loadReviewerConfig();
  }

  // Loads all available stories
  loadStories(users: User[]): void {
    const observableRequests: Observable<EasyStory[]>[] = users.map(user => {
      return this.storyService.getUserEasyStories(user);
    });
    zip(...observableRequests).subscribe(easyStoriesBundle => {
      const easyStories: EasyStory[] = [];
      for (const easyStoriesGroup of easyStoriesBundle) {
        easyStories.push(...easyStoriesGroup);
      }
      this.storyService.sortStoriesByTime(easyStories);
      this.easyStories = easyStories;
      this.loadingService.stop();
    });
  }

  loadReviewerConfig(): void {
    this.firebaseService.getReviewerConfig().subscribe(reviewerConfig => {
      this.projectIdList = this.storyService.getProjectIdList(reviewerConfig);
      this.loadStories(reviewerConfig.getUserList());
    });
  }

  filtersStories(projectId: string): void {
    this.filterChanges.next(projectId);
  }
}
