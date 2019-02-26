import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { zip } from 'rxjs';

import { Project, User } from '@/proto';
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
    private firebaseService: FirebaseService
  ) { }

  ngOnInit(): void {
    const projectId: number = +this.route.snapshot.paramMap.get('projectId');
    this.loadData(projectId);
  }

  loadData(projectId: number): void {
    zip(
      this.firebaseService.getUserList(),
      this.firebaseService.getProjectList()
    ).subscribe(data => {
      const userList: User[] = data[0];
      const projectList: Project[] = data[1];

      this.project = projectList.find(project => (project.getProjectId() === projectId));
      this.projectUsers = userList.filter(user => user.getProjectList().includes(projectId));
    });
  }
}
