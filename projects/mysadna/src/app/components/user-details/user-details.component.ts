import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { zip } from 'rxjs';

import { Project, User } from '@/proto';
import { FirebaseService } from '@/services';

@Component({
  selector: 'app-user-details',
  templateUrl: './user-details.component.html',
  styleUrls: ['./user-details.component.scss']
})
export class UserDetailsComponent implements OnInit {
  user: User;
  userProjects: Project[];

  constructor(
    private route: ActivatedRoute,
    private firebaseService: FirebaseService
  ) { }

  ngOnInit(): void {
    const userId: number = +this.route.snapshot.paramMap.get('userId');
    this.loadData(userId);
  }

  loadData(userId: number): void {
    zip(
      this.firebaseService.getUserList(),
      this.firebaseService.getProjectList()
    ).subscribe(data => {
      const userList: User[] = data[0];
      const projectList: Project[] = data[1];

      this.user = userList.find(user => user.getUserId() === userId);
      this.userProjects = projectList.filter(
        project => project.getContributorList().includes(userId)
      );
    });
  }

  getSkills(): string {
    return this.user.getSkillList().join(', ');
  }
}
