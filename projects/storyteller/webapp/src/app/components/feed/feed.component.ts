// A little bit of code formatting here
import { Component, OnInit, } from '@angular/core';
import { from } from 'rxjs';

import { StoryList, Screenshot, StoryItem } from '@/core/proto';
import { EncodingService, FirebaseService } from '@/core/services';
import { StoryService } from '@/core/services/story.service';

@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.scss']
})
export class FeedComponent {
  storylist: StoryList[];
  screenshots: Screenshot[];
  story: StoryItem;
  screenshot: Screenshot;

  constructor(
    private firebaseService: FirebaseService,
    private encodingService: EncodingService,
    private storyServiceToSingle: StoryService,
  ) {
    if (firebaseService.isOnline) {
      this.loadStory();
      this.loadScreenshots();
    } else {
      this.firebaseService.anonymousLogin().then(() => {
        this.loadStory();
        this.loadScreenshots();
      });
    }
    if (this.story != null && this.screenshot != null) {
      storyServiceToSingle.setData(this.story, this.screenshot);
    }
  }

  loadStory(): void {
    this.firebaseService.getstorylistAll().subscribe(storylist => {
      this.storylist = storylist;

    });
  }

  loadScreenshots(): void {
    this.firebaseService.getscreenshotAll().subscribe(screenshots => {
      this.screenshots = screenshots;
    });
  }

  shareStory(item: StoryItem, image: Screenshot): void {
    this.story = item;
    this.screenshot = image;
  }
}
