import { Component, OnInit, } from '@angular/core';
import { StoryList, Screenshot,StoryItem } from '@/core/proto';
import { EncodingService, FirebaseService } from '@/core/services';

import { from } from 'rxjs';
import { StoryService } from '@/core/services/story.service';
@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.scss']
})
export class FeedComponent implements OnInit {
  storylist: StoryList[];
  screenshots: Screenshot[];
  users: {};
  story:StoryItem;
  screenshot: Screenshot;

  constructor(
    private firebaseService: FirebaseService,
    private encodingService: EncodingService,
    storyServiceToSingle: StoryService,
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
      if(this.story != null && this.screenshot != null){
        storyServiceToSingle.setData(this.story, this.screenshot);
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
  sharestory(item:StoryItem,image:Screenshot): void{
    this.story = item;
    this.screenshot = image;
    }
}

//  
