import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { StoryList, Story,  Screenshot} from '@/core/proto';
import { EncodingService, FirebaseService } from '@/core/services';

import { from } from 'rxjs';
@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.scss']
})
export class FeedComponent implements OnInit {
  storylist: StoryList[];
  screenshots: Screenshot[];

  constructor(
    private firebaseService: FirebaseService,
    private encodingService: EncodingService,
  ) {
    if (firebaseService.isOnline) {
      this.loadStory();
      this.loadscreenshots();
    } else {
      this.firebaseService.anonymousLogin().then(() => {
        this.loadStory();
        this.loadscreenshots();
      });
    }
  }
  loadStory(): void {
    this.firebaseService.getstorylistAll().subscribe(storylist => {
      this.storylist = storylist;

    });
  }
  loadscreenshots(): void {
    this.firebaseService.getscreenshotAll().subscribe(screenshots => {
      this.screenshots = screenshots;
    });
  }
  ngOnInit() {
  }

}


