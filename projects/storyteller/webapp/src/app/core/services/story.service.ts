import { Injectable, OnInit } from '@angular/core';
import { StoryList, Screenshot, Story } from '../proto';
import { FirebaseService } from './firebase.service';
import { EncodingService } from './encoding.service';
import { zip } from 'rxjs';
import { NumberValueAccessor } from '@angular/forms/src/directives';

@Injectable({
  providedIn: 'root'
})
export class StoryService implements OnInit {

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
  ngOnInit() {
    if (this.firebaseService.isOnline) {
      this.setData();
    } else {
      this.firebaseService.anonymousLogin().then(() => {
        this.setData();
      });
    }
  }
  setData(): void {
    zip(
      this.firebaseService.getStorylistAll(),
      this.firebaseService.getScreenshotAll()
    ).subscribe(data => {
      this.storylist = data[0];
      this.screenshots = data[1];
    });
  }
  getStory(id: string): Story {
    let i, j: number;
    for (i = 0; i < this.storylist.length; i++) {
      for (j = 0; j < this.storylist[i].getStoryList().length; j++)
        if (this.storylist[i].getStoryList()[j].getId() == id) {
          return this.storylist[i].getStoryList()[j];
        }
    }
  }
  getScreenshot(filename: string): Screenshot {
    let i: number;
    for (i = 0; i < this.screenshots.length; i++) {
      if (this.screenshots[i].getFilename() == filename) {
        return this.screenshots[i];
      }
    }
  }
  // getStoryList(): StoryList[] {
  //   return this.storylist;
  // }
  // getScreenshots(): Screenshot[] {
  //   return this.screenshots;
  // }

}
