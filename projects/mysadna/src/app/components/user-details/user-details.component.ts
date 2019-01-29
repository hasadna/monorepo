import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';

import { User } from '@/models/user';
import { Project } from '@/models/project';
import { DataService } from '@/services/data.service';

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
    private dataService: DataService,
    private location: Location
  ) { }

  ngOnInit(): void {
    this.getUser();
  }

  getUser(): void {
    const userId: number = +this.route.snapshot.paramMap.get('userId');
    this.dataService.getUser(userId)
      .subscribe(user => {
        this.user = user;
        this.getUserProjects(userId);
      });
  }

  getUserProjects(userId: number): void {
    this.dataService.getUserProjects(userId)
      .subscribe(projects => {
        this.userProjects = projects;
      });
  }

  goBack(): void {
    this.location.back();
  }
}
