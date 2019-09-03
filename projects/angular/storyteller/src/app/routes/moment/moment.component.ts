import { Component, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { zip, Subscription } from 'rxjs';

import { User, Story, Moment } from '@/core/proto';
import {
  FirebaseService,
  NotificationService,
  StoryService,
  LoadingService,
  UserService,
} from '@/core/services';
import { EasyStory } from '@/core/interfaces';

@Component({
  selector: 'story-moment',
  templateUrl: './moment.component.html',
})
export class MomentComponent implements OnDestroy {
  storyId: string;
  momentId: string;
  easyStory: EasyStory;
  configSub = new Subscription();
  zipSub = new Subscription();

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private firebaseService: FirebaseService,
    private storyService: StoryService,
    private notificationService: NotificationService,
    public loadingService: LoadingService,
    private userService: UserService,
  ) {
    this.loadingService.isLoading = true;
    const email: string = this.activatedRoute.snapshot.params['email'];
    this.storyId = this.activatedRoute.snapshot.params['storyId'];
    this.momentId = this.activatedRoute.snapshot.params['momentId'];
    this.loadReviewerConfig(email);
  }

  loadStory(user: User): void {
    this.zipSub = zip(
      this.firebaseService.getStory(this.storyId, user.getEmail()),
      this.firebaseService.getMoment(this.momentId, user.getEmail(), this.storyId),
    ).subscribe(data => {
      const story: Story = data[0];
      const moment: Moment = data[1];
      if (!story) {
        this.crash('Story not found');
        return;
      }
      if (!moment) {
        this.crash('Moment not found');
        return;
      }
      if (story.getId() !== moment.getStoryId()) {
        this.crash('It is moment of different story');
        return;
      }
      this.easyStory = this.storyService.createEasyStory(story, moment, user);
      this.loadingService.isLoading = false;
    });
  }

  loadReviewerConfig(email: string): void {
    this.configSub = this.userService.onloadReviewerConfig().subscribe(reviewerConfig => {
      const user: User = this.storyService.getUser(email, reviewerConfig);
      if (!user) {
        this.crash('Invalid user');
        return;
      }
      this.loadStory(user);
    });
  }

  crash(message: string): void {
    this.notificationService.error(message);
    this.router.navigate(['/feed']);
  }

  ngOnDestroy() {
    this.configSub.unsubscribe();
    this.zipSub.unsubscribe();
  }
}
