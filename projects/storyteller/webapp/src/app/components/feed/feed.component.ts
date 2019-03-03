import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { StoryList, Story, Screenshot } from '@/core/proto';
import { EncodingService, FirebaseService, StoryService } from '@/core/services';

import { zip } from 'rxjs';
@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.scss']
})
export class FeedComponent {//implements OnInit{

  storylist: StoryList[];
  screenshots: Screenshot[];

  constructor(
    private firebaseService: FirebaseService,
    private encodingService: EncodingService,
    private storyService: StoryService,
  ) {
    if (firebaseService.isOnline) {
     this.setData();
    } else {
      this.firebaseService.anonymousLogin().then(() => {
        this.setData();
      });
  }
}
// ngOnInit() {
//   this.storylist = this.storyService.getStoryList();
//   this.screenshots = this.storyService.getScreenshots();
// }
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

