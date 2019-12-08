import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, Subscription, Subject, of } from 'rxjs';

import { ReviewerConfig, User, Story } from '@/core/proto';
import { FirebaseService } from './firebase.service';
import { AuthService } from './auth.service';
import { StoryService } from './story.service';

@Injectable()
export class UserService {
  isUserLoading: boolean = true;
  isStoryListLoading: boolean = true;
  user: User;
  reviewerConfig: ReviewerConfig;
  storyList: Story[];
  reviewerConfigChanges = new Subject<ReviewerConfig>();
  storyListChanges = new Subject<Story[]>();
  private configSub = new Subscription();
  private storyListSub = new Subscription();

  constructor(
    private router: Router,
    private authService: AuthService,
    private firebaseService: FirebaseService,
    private storyService: StoryService,
  ) {
    this.authService.onlineChanges.subscribe(isOnline => {
      if (isOnline) {
        this.connectReviewerConfig();
        this.connectStoryList();
      } else {
        this.router.navigate(['/']);
        this.destroy();
      }
    });
  }

  onloadReviewerConfig(): Observable<ReviewerConfig> {
    if (!this.isUserLoading) {
      return of(this.reviewerConfig);
    } else {
      return this.reviewerConfigChanges;
    }
  }

  onloadStoryList(): Observable<Story[]> {
    if (!this.isStoryListLoading) {
      return of(this.storyList);
    } else {
      return this.storyListChanges;
    }
  }

  private connectReviewerConfig(): void {
    this.isUserLoading = true;
    this.configSub = this.firebaseService.getReviewerConfig().subscribe(reviewerConfig => {
      this.reviewerConfig = reviewerConfig;
      this.user = this.storyService.getUser(this.authService.email, reviewerConfig);
      this.isUserLoading = false;
      this.reviewerConfigChanges.next(reviewerConfig);
    });
  }

  private connectStoryList(): void {
    this.isStoryListLoading = true;
    this.storyListSub = this.firebaseService.getStoryList(this.authService.email)
      .subscribe(storyList => {
        this.storyList = storyList;
        this.isStoryListLoading = false;
        this.storyListChanges.next(storyList);
      });
  }

  destroy(): void {
    this.configSub.unsubscribe();
    this.storyListSub.unsubscribe();
  }
}
