import { Component } from '@angular/core';

import { EncodingService, FirebaseService } from '@/services';
import { Data, User, Project } from '@/proto';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  data: Data;
  userList: User[];
  projectList: Project[];

  constructor(
    private encodingService: EncodingService,
    private firebaseService: FirebaseService,
  ) {
    this.firebaseService.InitData();
    this.data = new Data();
    this.firebaseService.getUserList()
    .subscribe(userList => {
      this.userList = userList;
      console.log(this.userList);
      this.data.setUserList(this.userList);
    });
    this.firebaseService.getProjectList()
    .subscribe(projectList => {
      this.projectList = projectList;
      console.log(this.projectList);
      this.data.setProjectList(this.projectList);
    });
  }
}
