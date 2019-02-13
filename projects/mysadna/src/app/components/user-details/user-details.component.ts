import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';

import { User, Project, Contribution } from '@/proto';
import { FirebaseService } from '@/services';

@Component({
  selector: 'app-user-details',
  templateUrl: './user-details.component.html',
  styleUrls: ['./user-details.component.scss'],
})
export class UserDetailsComponent implements OnInit {
  user: User;

  userProjects: Project[];

  constructor(
    private route: ActivatedRoute,
    private firebaseService: FirebaseService,
    private location: Location
  ) { }

  ngOnInit(): void {
    this.getUser();
  }

  getUser(): void {
    const userId: number = +this.route.snapshot.paramMap.get('userId');
    this.firebaseService.getUserList()
      .subscribe(userList => {
        this.user = userList.find(user => user.getUserId() === userId);
        this.getUserProjects(userId);
      });
  }

  getUserProjects(userId: number): void {
    this.firebaseService.getProjectList()
      .subscribe(projects => {
        this.userProjects = projects.filter(project => project.getContributorList().includes(userId));
      });
  }

  goBack(): void {
    this.location.back();
  }
}
