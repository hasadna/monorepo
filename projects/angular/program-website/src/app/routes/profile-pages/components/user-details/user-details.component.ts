import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { Project, User, SocialNetwork } from '@/core/proto';
import { FirebaseService } from '@/core/services';

@Component({
  selector: 'profile-pages-user-details',
  templateUrl: './user-details.component.html',
  styleUrls: ['./user-details.component.scss'],
})
export class UserDetailsComponent implements OnInit {
  user: User;
  userProjects: Project[];
  types: string[] = Object.keys(SocialNetwork.Type);

  constructor(
    private route: ActivatedRoute,
    private firebaseService: FirebaseService,
  ) { }

  ngOnInit(): void {
    const userId: string = this.route.snapshot.paramMap.get('userId');
    this.loadData(userId);
  }

  loadData(userId: string): void {
    this.firebaseService.getReviewerConfig()
      .subscribe(reviewerConfig => {
        const userList: User[] = reviewerConfig.getUserList();
        const projectList: Project[] = reviewerConfig.getProjectList();

        this.user = userList.find(user => user.getId() === userId);
        this.userProjects = projectList.filter(
          project => project.getUserIdList().includes(userId),
        );
      });
  }

  getSkills(): string {
    return this.user.getSkillList().join(', ');
  }
}
