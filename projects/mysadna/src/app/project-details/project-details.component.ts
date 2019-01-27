import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';

import { User } from '../models/user';
import { Project } from '../models/project';
import { DataService } from '../services/data.service';

@Component({
  selector: 'app-project-details',
  templateUrl: './project-details.component.html',
  styleUrls: ['./project-details.component.scss']
})
export class ProjectDetailsComponent implements OnInit {
  project: Project;

  projectUsers: User[];

  constructor(
    private route: ActivatedRoute,
    private dataService: DataService,
    private location: Location
  ) { }

  ngOnInit() {
    this.getProject();
  }

  getProject(): void {
    const projectId: number = +this.route.snapshot.paramMap.get('projectId');
    this.dataService.getProject(projectId)
      .subscribe(project => {
        this.project = project;
        this.getProjectUsers(projectId);
      });
  }

  getProjectUsers(projectId: number): void {
    this.dataService.getProjectUsers(projectId)
      .subscribe(users => {
        this.projectUsers = users;
      });
  }

  goBack(): void {
    this.location.back();
  }
}
