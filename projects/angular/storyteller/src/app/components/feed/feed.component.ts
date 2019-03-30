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
  projectList: string[];

  constructor(
    private firebaseService: FirebaseService,
  ) {
    if (firebaseService.isOnline) {
      this.loadUsers();
      this.getProjects();
    } else {
      this.firebaseService.anonymousLogin().then(() => {
        this.loadUsers();
        this.getProjects();
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

  loadUsers(): void {
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

  getProjects(): void {
    this.firebaseService.getReviewerConfig().subscribe(reviewerConfig => {
      const projectIdList: string[] = reviewerConfig.getProjectList().map(
        project => project.getId(),
      );
      this.projectList = projectIdList;
    });
  }
}
