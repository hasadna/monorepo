import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FirebaseService } from '@/core/services';
import { User, StoryList, Screenshot } from '@/core/proto';
import { zip } from 'rxjs';

@Component({
  selector: 'app-user-info',
  templateUrl: './user-info.component.html',
  styleUrls: ['./user-info.component.scss']
})
export class UserInfoComponent implements OnInit {
  userEmail: string;
  user: User;
  storylist: StoryList[];
  screenshots: Screenshot[];
  projectList: string[];
  isLoading = true;

  constructor(
    private activatedRoute: ActivatedRoute,
    private firebaseService: FirebaseService
  ) { }

  ngOnInit() {
    this.userEmail = this.activatedRoute.snapshot.params['userEmail'];
    this.getUserByEmail(this.userEmail);
    this.getProjects();
  }

  getUserByEmail(userEmail: string): void {
    this.firebaseService.getReviewerConfig().subscribe(userList => {
      for (const user of userList.getUserList()) {
        if (user.getEmail() === userEmail) {
          this.user = user;
          this.getData(this.user.getEmail());
        }
      }
    });
  }

  getData(author: string): void {
    zip(
      this.firebaseService.getUserStories(author),
      this.firebaseService.getUserScreenshot(author)
    ).subscribe(data => {
      this.storylist = data[0];
      this.screenshots = data[1];
      this.isLoading = false;
    });
  }

  getProjects(): void {
    this.firebaseService.getReviewerConfig().subscribe(reviewerConfig => {
      const projectIdList: string[] = reviewerConfig.getProjectList().map(
        project => project.getId()
      );
      this.projectList = projectIdList;
    });
  }
}
