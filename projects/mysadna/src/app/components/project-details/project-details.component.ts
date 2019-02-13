import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';

import { User, Project } from '@/proto';
import { FirebaseService } from '@/services';

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
    private firebaseService: FirebaseService,
    private location: Location
  ) { }

  ngOnInit() {
    this.getProject();
  }

  getProject(): void {
    const projectId: number = +this.route.snapshot.paramMap.get('projectId');
    this.firebaseService.getProjectList()
      .subscribe(projectList => {
        this.project = projectList.find(project => (project.getProjectId() === projectId));
        this.getProjectUsers(projectId);
      });
  }

  getProjectUsers(projectId: number): void {
    this.firebaseService.getUserList()
      .subscribe(users => {
        this.projectUsers = users.filter(user => user.getProjectList().includes(projectId));
      });
  }

  goBack(): void {
    this.location.back();
  }
}
