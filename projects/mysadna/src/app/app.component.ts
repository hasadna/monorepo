import { Component } from '@angular/core';

import { EncodingService, FirebaseService } from '@/services';
import { Data, User, Project } from '@/proto';
import { zip } from 'rxjs';

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
    this.data = new Data();
    zip(
      this.firebaseService.getUserList(),
      this.firebaseService.getProjectList()
    ).subscribe(data => {
      this.userList = data[0];
      this.projectList = data[1];

      this.data.setUserList(this.userList);
      this.data.setProjectList(this.projectList);
    });
  }
}
