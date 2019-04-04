import { Component } from '@angular/core';
import { Observable, zip } from 'rxjs';

import { Screenshot, User, Story } from '@/core/proto';
import { FirebaseService, StoryService } from '@/core/services';
import { EasyStory } from '@/shared';

@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.scss'],
})
export class FeedComponent {
  isLoading: boolean = true;
  projectIdList: string[];
  easyStories: EasyStory[];

  constructor(
    private firebaseService: FirebaseService,
    private storyService: StoryService,
  ) {
    this.loadReviewerConfig();
  }

  // Loads all available stories
  loadStories(users: User[]): void {
    const observableRequests: Observable<EasyStory[]>[] = users.map(user => {
      return this.loadUserStories(user);
    });
    zip(...observableRequests).subscribe(easyStoriesBundle => {
      this.easyStories = [];
      for (const easyStories of easyStoriesBundle) {
        this.easyStories.push(...easyStories);
      }
      this.storyService.sortStoriesByTime(this.easyStories);
      this.isLoading = false;
    });
  }

  // Loads stories of specific user
  loadUserStories(user: User): Observable<EasyStory[]> {
    return new Observable(observer => {
      zip(
        this.firebaseService.getUserStories(user.getEmail()),
        this.firebaseService.getUserScreenshot(user.getEmail()),
      ).subscribe(data => {
        const stories: Story[] = this.storyService.getStories(data[0]);
        const screenshots: Screenshot[] = this.storyService.filterScreenshots(data[1], stories);
        const easyStories: EasyStory[] = this.storyService.createEasyStories(
          screenshots,
          stories,
          user,
        );
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

  // Filters stories by project id
  loadProjectStories(projectId: string): void {
    // TODO: implement this
  }
}
