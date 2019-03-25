import { Component } from '@angular/core';

import { Screenshot, Story, StoryList, User } from '@/core/proto';
import { FirebaseService } from '@/core/services';

import { zip } from 'rxjs';
@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.scss'],
})
export class FeedComponent {
  storylist: StoryList[] = [];
  screenshots: Screenshot[] = [];
  users: User[];
  isLoading = true;

  constructor(
    private firebaseService: FirebaseService,
  ) {
    if (firebaseService.isOnline) {
      this.loadusers();

    } else {
      this.firebaseService.anonymousLogin().then(() => {
        this.loadusers();
      });
    }
  }

  setData(user: string): void {
    zip(
      this.firebaseService.getUserStories(user),
      this.firebaseService.getUserScreenshot(user),
    ).subscribe(data => {
      this.storylist.push.apply(this.storylist, data[0]);
      this.screenshots.push.apply(this.screenshots, data[1]);
      this.isLoading = false;
    });
  }

  loadusers(): void {
    this.firebaseService.getReviewerConfig().subscribe(userData => {
      this.users = userData.getUserList();
      this.getData(this.users);
    });
  }

  getData(users: User[]) {
    for (const user of this.users) {
      this.setData(user.getEmail());
    }
  }

  getRouterLink(story: Story, screenshot: Screenshot): string {
    const id: string = story.getId();
    const filename: string = screenshot.getFilename();
    const author: string = story.getAuthor();
    return `/single-screenshot/${id}/${filename}/${author}`;
  }
}
