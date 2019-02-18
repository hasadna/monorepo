import { Component, OnInit } from '@angular/core';

import { Project } from '@/proto';
import { FirebaseService } from '@/services';

@Component({
  selector: 'app-projects',
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.scss']
})
export class ProjectsComponent implements OnInit {
  projects: Project[];

  constructor(private firebaseService: FirebaseService) { }

  ngOnInit() {
    this.getAllProjects();
  }

  getAllProjects(): void {
    this.firebaseService.getProjectList()
      .subscribe(projectList => {
        this.projects = projectList;
      });
  }
}
