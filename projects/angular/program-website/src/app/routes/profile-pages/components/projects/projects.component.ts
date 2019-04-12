import { Component, OnInit } from '@angular/core';

import { Project } from '@/core/proto';
import { FirebaseService } from '@/core/services';

@Component({
  selector: 'profile-pages-projects',
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.scss'],
})
export class ProjectsComponent implements OnInit {
  projects: Project[];

  constructor(private firebaseService: FirebaseService) { }

  ngOnInit() {
    this.getAllProjects();
  }

  getAllProjects(): void {
    this.firebaseService.getReviewerConfig()
      .subscribe(reviewerConfig => {
        this.projects = reviewerConfig.getProjectList();
      });
  }
}
