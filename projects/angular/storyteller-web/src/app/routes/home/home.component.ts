import { Component } from '@angular/core';
import { Observable, zip, Subject } from 'rxjs';

import { User, Story } from '@/core/proto';
import { FirebaseService, StoryService, LoadingService } from '@/core/services';
import { EasyStory } from '@/shared';

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
    this.loadingService.isLoading = true;
    this.loadReviewerConfig();
  }

  // Loads all available stories
  loadStories(users: User[]): void {
    const observableRequests: Observable<EasyStory[]>[] = users.map(user => {
      return this.loadUserStories(user);
    });
    zip(...observableRequests).subscribe(easyStoriesBundle => {
      const easyStories: EasyStory[] = [];
      for (const easyStoriesGroup of easyStoriesBundle) {
        easyStories.push(...easyStoriesGroup);
      }
      this.storyService.sortStoriesByTime(easyStories);
      this.easyStories = easyStories;
      this.loadingService.isLoading = false;
    });
  }

  // Loads stories of specific user
  loadUserStories(user: User): Observable<EasyStory[]> {
    return new Observable(observer => {
      this.firebaseService.getUserStories(user.getEmail()).subscribe(storyLists => {
        const stories: Story[] = this.storyService.getStories(storyLists);
        const easyStories: EasyStory[] = this.storyService.createEasyStories(stories, user);
        observer.next(easyStories);
      });
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
