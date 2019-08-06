import { Component, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';

import { User } from '@/core/proto';
import {
  NotificationService,
  StoryService,
  LoadingService,
  FirestoryService,
  AuthService,
} from '@/core/services';
import { EasyStory } from '@/core/interfaces';

@Component({
  selector: 'user-info',
  templateUrl: './user-info.component.html',
  styleUrls: ['./user-info.component.scss'],
})
export class UserInfoComponent implements OnDestroy {
  easyStories: EasyStory[];
  user: User;
  email: string;
  configSub = new Subscription();
  storySub = new Subscription();

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private storyService: StoryService,
    private notificationService: NotificationService,
    public loadingService: LoadingService,
    private firestoryService: FirestoryService,
    private authService: AuthService,
  ) {
    this.loadingService.isLoading = true;
    this.email = this.activatedRoute.snapshot.params['email'];
    this.loadReviewerConfig(this.email);
  }

  loadReviewerConfig(email: string): void {
    this.configSub = this.authService.onloadReviewerConfig().subscribe(reviewerConfig => {
      this.user = this.storyService.getUser(email, reviewerConfig);
      if (!this.user) {
        this.crash('User not found');
        return;
      }
      this.storySub = this.firestoryService.getUserEasyStories(this.user)
        .subscribe(easyStories => {
          this.easyStories = easyStories;
          this.loadingService.isLoading = false;
        });
    });
  }

  crash(message: string): void {
    this.notificationService.error(message);
    this.router.navigate(['/feed']);
  }

  ngOnDestroy() {
    this.configSub.unsubscribe();
    this.storySub.unsubscribe();
    this.firestoryService.unsubscribe();
  }
}
