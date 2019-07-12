import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';

import { User } from '@/core/proto';
import {
  FirebaseService,
  NotificationService,
  StoryService,
  LoadingService,
} from '@/core/services';
import { EasyStory } from '@/core/interfaces';

@Component({
  selector: 'user-info',
  templateUrl: './user-info.component.html',
  styleUrls: ['./user-info.component.scss'],
})
export class UserInfoComponent {
  projectIdList: string[];
  easyStories: EasyStory[];
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
    this.loadingService.load();
    this.email = this.activatedRoute.snapshot.params['email'];
    this.loadReviewerConfig(this.email);
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

      this.storyService.getUserEasyStories(this.user).subscribe(easyStories => {
        this.easyStories = easyStories;
        this.loadingService.stop();
      });
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
