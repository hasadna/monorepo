import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { zip } from 'rxjs';

import { Screenshot, User, Story } from '@/core/proto';
import { FirebaseService, NotificationService, StoryService } from '@/core/services';
import { EasyStory } from '@/shared';

@Component({
  selector: 'single-item',
  templateUrl: './single-item.component.html',
  styleUrls: ['./single-item.component.scss'],
})
export class SingleItemComponent {
  isLoading: boolean = true;
  isCrashError: boolean = false;
  storyId: string;
  itemId: string;
  easyStory: EasyStory;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private firebaseService: FirebaseService,
    private storyService: StoryService,
    private notificationService: NotificationService,
  ) {
    const email: string = this.activatedRoute.snapshot.params['email'];
    this.storyId = this.activatedRoute.snapshot.params['storyId'];
    this.itemId = this.activatedRoute.snapshot.params['itemId'];
    this.loadReviewerConfig(email);
  }

  loadStories(user: User): void {
    zip(
      this.firebaseService.getUserStories(user.getEmail()),
      this.firebaseService.getUserScreenshot(user.getEmail()),
    ).subscribe(data => {
      const stories: Story[] = this.storyService.getStories(data[0]);
      const story: Story = this.getStory(stories);
      const screenshots: Screenshot[] = this.storyService.filterScreenshots(data[1], stories);
      if (!this.isCrashError) {
        const easyStories: EasyStory[] = this.storyService.createEasyStoryItemList(
          screenshots,
          story,
          user,
        );
        this.easyStory = this.getEasyStory(easyStories);
        this.isLoading = false;
      }
    });
  }

  // Searches a story in story list by story id
  getStory(stories: Story[]): Story {
    for (const story of stories) {
      if (story.getId() === this.storyId) {
        return story;
      }
    }
    this.crash('Invalid story id');
  }

  // Searches an easyStory by itemId
  getEasyStory(easyStories: EasyStory[]): EasyStory {
    for (const easyStory of easyStories) {
      if (easyStory.itemId === this.itemId) {
        return easyStory;
      }
    }
    this.crash('Invalid item id');
  }

  loadReviewerConfig(email: string): void {
    this.firebaseService.getReviewerConfig().subscribe(reviewerConfig => {
      const user: User = this.storyService.getUser(email, reviewerConfig);
      if (!user) {
        this.crash('Invalid user');
        return;
      }
      this.loadStories(user);
    });
  }

  crash(message: string): void {
    this.notificationService.error(message);
    this.isCrashError = true;
    this.router.navigate(['/home']);
  }
}
