import { Injectable } from '@angular/core';
import { AngularFirestore, AngularFirestoreCollection } from 'angularfire2/firestore';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { User, Project, Contribution, Data } from '../proto';
import { EncodingService } from './encoding.service';

interface FirebaseElement {
  proto: string;
}

@Injectable()
export class FirebaseService {
  private protobin: AngularFirestoreCollection<FirebaseElement>;
  private userList: AngularFirestoreCollection<FirebaseElement>;
  private projectList: AngularFirestoreCollection<FirebaseElement>;
  data: Data;
  users: User[] = [];
  projects: Project[] = [];
  tempUser: User;
  tempProject: Project;

  constructor(
    private db: AngularFirestore,
    private encodingService: EncodingService,
  ) {
    this.protobin = this.db.collection('protobin');
    this.projectList = this.db.collection('protobin/data/project-list');
    this.userList = this.db.collection('protobin/data/user-list');
  }

  InitData(): void {
    this.data = new Data();
    this.getUserList().subscribe(users => {
      this.data.setUserList(users);
    });
    this.getProjectList().subscribe(projects =>  {
      this.data.setProjectList(projects);
    });
  }

  getUserList(): Observable<User[]> {
    return this.userList.snapshotChanges().pipe(
      map(action => action.map(a => {
        const firebaseElement = a.payload.doc.data() as FirebaseElement;

        if (firebaseElement === undefined) {
          // Element not found
          return;
        }

        this.tempUser = this.convertFirebaseElementToUser(firebaseElement);

        if (this.users.find(user => user.getUserId() === this.tempUser.getUserId())) {
          const userIndex = this.users.findIndex(user => user.getUserId() === this.tempUser.getUserId());
          this.users[userIndex] = this.tempUser;
        } else {
          this.users.push(this.tempUser);
        }
        return this.tempUser;
      }))
    );
  }

  getProjectList(): Observable<Project[]> {
    return this.projectList.snapshotChanges().pipe(
      map(action => action.map(a => {
        const firebaseElement = a.payload.doc.data() as FirebaseElement;

        if (firebaseElement === undefined) {
          // Element not found
          return;
        }

        this.tempProject = this.convertFirebaseElementToProject(firebaseElement);

        if (this.projects.find(project => project.getProjectId() === this.tempProject.getProjectId())) {
          const projectIndex = this.projects.findIndex(project => project.getProjectId() === this.tempProject.getProjectId());
          this.projects[projectIndex] = this.tempProject;
        } else {
          this.projects.push(this.tempProject);
        }
        return this.tempProject;
      }))
    );
  }

  private convertFirebaseElementToUser(firebaseElement: FirebaseElement): User {
    // Convert firebaseElement to binary
    const binary: Uint8Array = this.encodingService
      .decodeBase64StringToUint8Array(firebaseElement.proto);
    // Convert binary to user
    const user: User = User.deserializeBinary(binary);

    return user;
  }

  private convertFirebaseElementToProject(firebaseElement: FirebaseElement): Project {
    // Convert firebaseElement to binary
    const binary: Uint8Array = this.encodingService
      .decodeBase64StringToUint8Array(firebaseElement.proto);
    // Convert binary to project
    const project: Project = Project.deserializeBinary(binary);

    return project;
  }

  // private convertFirebaseElementToData(firebaseElement: FirebaseElement): Data {
  //   // Convert firebaseElement to binary
  //   const binary: Uint8Array = this.encodingService
  //     .decodeBase64StringToUint8Array(firebaseElement.proto);
  //   // Convert binary to data
  //   const data: Data = Data.deserializeBinary(binary);

  //   return data;
  // }
}
