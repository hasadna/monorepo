import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { zip } from 'rxjs';

import { Screenshot, User, Story } from '@/core/proto';
import { FirebaseService, StoryService } from '@/core/services';
import { EasyStory } from '@/shared';

@Component({
  selector: 'single-item',
  templateUrl: './single-item.component.html',
  styleUrls: ['./single-item.component.scss'],
})
export class SingleItemComponent {
  isLoading = true;
  storyId: string;
  easyStory: EasyStory;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private firebaseService: FirebaseService,
    private storyService: StoryService,
  ) {
    const email: string = this.activatedRoute.snapshot.params['email'];
    this.storyId = this.activatedRoute.snapshot.params['storyId'];
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
      this.easyStory = this.storyService.createEasyStory(
        screenshots,
        story,
        user,
      );
      this.isLoading = false;
    });
  }

  // Searches a story in story list by story id
  getStory(stories: Story[]): Story {
    for (const story of stories) {
      if (story.getId() === this.storyId) {
        return story;
      }
    }

    console.error('Story not found');
    this.router.navigate(['/home']);
  }

  loadReviewerConfig(email: string): void {
    this.firebaseService.getReviewerConfig().subscribe(reviewerConfig => {
      const user: User = this.storyService.getUser(email, reviewerConfig);
      this.loadStories(user);
    });
  }
}
