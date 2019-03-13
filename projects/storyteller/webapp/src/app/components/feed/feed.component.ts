import { Component } from '@angular/core';
import { StoryList, Story, Screenshot } from '@/core/proto';
import { EncodingService, FirebaseService } from '@/core/services';

import { zip } from 'rxjs';
@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.scss']
})
export class FeedComponent {
  storylist: StoryList[] = [];
  screenshots: Screenshot[] = [];
  users: string[];
  isLoading = true;

  constructor(
    private firebaseService: FirebaseService,
    private encodingService: EncodingService,
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
      this.firebaseService.getUserScreenshot(user)
    ).subscribe(data => {
      this.storylist.push.apply(this.storylist, data[0]);
      this.screenshots.push.apply(this.screenshots, data[1]);
      this.isLoading = false;
    });
  }
  loadusers(): void {
    this.firebaseService.getUsersAll().subscribe(userData => {
      this.users = userData.users;
      this.getData(this.users);
    });
  }
  getData(users: string[]) {
    for (const user of this.users) {
      this.setData(user);
    }
  }
}

