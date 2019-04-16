import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { Project, User } from '@/core/proto';
import { FirebaseService } from '@/core/services';

@Component({
  selector: 'profile-pages-project-details',
  templateUrl: './project-details.component.html',
  styleUrls: ['./project-details.component.scss'],
})
export class ProjectDetailsComponent implements OnInit {
  project: Project;
  projectUsers: User[];

  constructor(
    private route: ActivatedRoute,
    private firebaseService: FirebaseService,
  ) { }

  ngOnInit(): void {
    const projectId: string = this.route.snapshot.paramMap.get('projectId');
    this.loadData(projectId);
  }

  loadData(projectId: string): void {
   this.firebaseService.getReviewerConfig()
    .subscribe(reviewerConfig => {
      const userList: User[] = reviewerConfig.getUserList();
      const projectList: Project[] = reviewerConfig.getProjectList();

      this.project = projectList.find(project => (project.getId() === projectId));
      this.projectUsers = userList.filter(
        user => user.getProjectIdList().includes(projectId),
      );
    });
  }
}
