import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { zip } from 'rxjs';

import { Story, Screenshot, StoryList } from '@/core/proto';
import { FirebaseService } from '@/core/services';

@Component({
  selector: 'app-single-screenshot',
  templateUrl: './single-screenshot.component.html',
  styleUrls: ['./single-screenshot.component.scss']
})
export class SingleScreenshotComponent implements OnInit {
  isLoading = true;
  storyId: string;
  screenshotFilename: string;
  author: string;
  story: Story;
  screenshot: Screenshot;

  constructor(
    private activatedRoute: ActivatedRoute,
    private firebaseService: FirebaseService
  ) { }

  ngOnInit() {
    this.storyId = this.activatedRoute.snapshot.params['storyId'];
    this.screenshotFilename = this.activatedRoute.snapshot.params['screenshot'];
    this.author = this.activatedRoute.snapshot.params['storyAuthor'];

    zip(
      this.firebaseService.getUserStories(this.author),
      this.firebaseService.getUserScreenshot(this.author)
    ).subscribe(data => {
      this.getStory(this.storyId, data[0]);
      this.getScreenshot(this.screenshotFilename, data[1]);
      this.isLoading = false;
    });
  }
  getStory(storyId: string, storyLists: StoryList[]): void {
    for (const storyList of storyLists) {
      for (const story of storyList.getStoryList()) {
        if (story.getId() === storyId) {
          this.story = story;
          return;
        }
      }
    }
  }
  getScreenshot(screenshotFilename: string, screenshots: Screenshot[]): void {
    for (const screenshot of screenshots) {
      if (screenshot.getFilename() === screenshotFilename) {
        this.screenshot = screenshot;
        return;
      }
    }
  }
}

