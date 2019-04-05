import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { zip } from 'rxjs';

import { Screenshot, User, Story } from '@/core/proto';
import { FirebaseService, StoryService } from '@/core/services';
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
  user: User;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private firebaseService: FirebaseService,
    private storyService: StoryService,
  ) {
    const email: string = this.activatedRoute.snapshot.params['email'];
    this.loadReviewerConfig(email);
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
      this.isLoading = false;
    });
  }

  loadReviewerConfig(email: string): void {
    this.firebaseService.getReviewerConfig().subscribe(reviewerConfig => {
      // Get user and project id list
      this.user = this.storyService.getUser(email, reviewerConfig);
      if (!this.user) {
        console.error('User not found');
        this.router.navigate(['/home']);
        return;
      }
      this.projectIdList = this.storyService.getProjectIdList(reviewerConfig);

      this.loadStories();
    });
  }

  // Filters stories by project id
  loadProjectStories(projectId: string): void {
    // TODO: implement this
  }
}
