import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { StoryList, Story, Screenshot } from '@/core/proto';
import { EncodingService, FirebaseService } from '@/core/services';

import { zip } from 'rxjs';
@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.scss']
})
export class FeedComponent {
  storylist: StoryList[];
  screenshots: Screenshot[];

  constructor(
    private firebaseService: FirebaseService,
    private encodingService: EncodingService,
  ) {
    if (firebaseService.isOnline) {
     this.setData();
    } else {
      this.firebaseService.anonymousLogin().then(() => {
        this.setData();
      });
  }
}
  setData(): void{
    zip(
      this.firebaseService.getStorylistAll(),
      this.firebaseService.getScreenshotAll()
    ).subscribe(data => {
      this.storylist = data[0];
      this.screenshots = data[1];
    });
  }
}

