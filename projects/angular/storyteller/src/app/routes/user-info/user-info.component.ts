import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';

import { User, Story } from '@/core/proto';
import {
  FirebaseService,
  NotificationService,
  StoryService,
  LoadingService,
} from '@/core/services';
import { EasyStory } from '@/shared';

@Component({
  selector: 'user-info',
  templateUrl: './user-info.component.html',
  styleUrls: ['./user-info.component.scss'],
})
export class UserInfoComponent {
  projectIdList: string[];
  easyStories: EasyStory[];
  filteredEasyStories: EasyStory[];
  user: User;
  email: string;
  filterChanges = new Subject<string>();

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private firebaseService: FirebaseService,
    private storyService: StoryService,
    private notificationService: NotificationService,
    public loadingService: LoadingService,
  ) {
    this.loadingService.isLoading = true;
    this.email = this.activatedRoute.snapshot.params['email'];
    this.loadReviewerConfig(this.email);
  }

  loadStories(): void {
    this.firebaseService.getUserStories(this.user.getEmail()).subscribe(storyLists => {
      const stories: Story[] = this.storyService.getStories(storyLists);
      this.easyStories = this.storyService.createEasyStories(stories, this.user);
      this.storyService.sortStoriesByTime(this.easyStories);
      this.filteredEasyStories = this.easyStories.slice();
      this.loadingService.isLoading = false;
    });
  }

  loadReviewerConfig(email: string): void {
    this.firebaseService.getReviewerConfig().subscribe(reviewerConfig => {
      // Get user and project id list
      this.user = this.storyService.getUser(email, reviewerConfig);
      if (!this.user) {
        this.crash('User not found');
        return;
      }
      this.projectIdList = this.storyService.getProjectIdList(reviewerConfig);

      this.loadStories();
    });
  }

  filtersStories(projectId: string): void {
    this.filterChanges.next(projectId);
  }

  crash(message: string): void {
    this.notificationService.error(message);
    this.router.navigate(['/home']);
  }
}
