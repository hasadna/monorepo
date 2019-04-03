import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { zip } from 'rxjs';

import { Screenshot, Story, StoryList, User } from '@/core/proto';
import { FirebaseService } from '@/core/services';

@Component({
  selector: 'app-single-screenshot',
  templateUrl: './single-screenshot.component.html',
  styleUrls: ['./single-screenshot.component.scss'],
})
export class SingleScreenshotComponent implements OnInit {
  isLoading = true;
  storyId: string;
  screenshotFilename: string;
  author: string;
  story: Story;
  screenshot: Screenshot;
  user: User;
  constructor(
    private activatedRoute: ActivatedRoute,
    private firebaseService: FirebaseService,
  ) { }

  ngOnInit() {
    this.storyId = this.activatedRoute.snapshot.params['storyId'];
    this.screenshotFilename = this.activatedRoute.snapshot.params['screenshot'];
    this.author = this.activatedRoute.snapshot.params['storyAuthor'];
    this.getUser(this.author);
  }

  getData(author: string): void {
    zip(
      this.firebaseService.getUserStories(author),
      this.firebaseService.getUserScreenshot(author),
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

  getUser(userEmail: string): void {
    this.firebaseService.getReviewerConfig().subscribe(userList => {
      for (const user of userList.getUserList()) {
        if (user.getEmail() === userEmail) {
          this.user = user;
          this.getData(this.user.getEmail());
        }
      }
    });
  }
}
