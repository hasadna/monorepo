import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { zip } from 'rxjs';

import { Screenshot, User, Story } from '@/core/proto';
import { FirebaseService, NotificationService, StoryService } from '@/core/services';
import { EasyStory } from '@/shared';

@Component({
  selector: 'user-info',
  templateUrl: './user-info.component.html',
  styleUrls: ['./user-info.component.scss'],
})
export class UserInfoComponent {
  isLoading: boolean = true;
  projectIdList: string[];
  easyStories: EasyStory[];
  filteredEasyStories: EasyStory[];
  user: User;
  email: string;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private firebaseService: FirebaseService,
    private storyService: StoryService,
    private notificationService: NotificationService,
  ) {
    this.email = this.activatedRoute.snapshot.params['email'];
    this.loadReviewerConfig(this.email);
  }

  loadStories(): void {
    zip(
      this.firebaseService.getUserStories(this.user.getEmail()),
      this.firebaseService.getUserScreenshot(this.user.getEmail()),
    ).subscribe(data => {
      const stories: Story[] = this.storyService.getStories(data[0]);
      const screenshots: Screenshot[] = this.storyService.filterScreenshots(data[1], stories);
      this.easyStories = this.storyService.createEasyStories(screenshots, stories, this.user);
      this.storyService.sortStoriesByTime(this.easyStories);
      this.filteredEasyStories = this.easyStories.slice();
      this.isLoading = false;
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

  // Filters stories by project id
  loadProjectStories(projectId: string): void {
    this.filteredEasyStories = (projectId !== 'all') ?
      this.storyService.getFilteredStories(projectId, this.easyStories)
      : this.easyStories.slice();
  }

  crash(message: string): void {
    this.notificationService.error(message);
    this.router.navigate(['/home']);
  }
}
