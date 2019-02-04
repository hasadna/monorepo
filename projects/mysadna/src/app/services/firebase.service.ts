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

  constructor(
    private db: AngularFirestore,
    private encodingService: EncodingService,
  ) {
    this.protobin = this.db.collection('protobin');
  }

  getData(): Observable<Data> {
    return this.protobin
      .doc('data')
      .snapshotChanges()
      .pipe(
        map(action => {
          const firebaseElement = action.payload.data() as FirebaseElement;
          if (firebaseElement === undefined) {
            // Element not found
            return;
          }
          // this.userList = this.db.collection('protobin').doc('data').collection('user-list');
          // this.getUserList();
          // this.projectList = this.db.collection('protobin').doc('data').collection('project-list');
          // this.getProjectList();
          return this.convertFirebaseElementToData(firebaseElement);
        })
      );
  }

  // getUserList(): Observable<User[]> {
  //   users: User[];
  //   this.getUser();
  // }

  // getUser(): Observable<User> {
  //   return this.userList
  //     .doc('user1')
  //     .snapshotChanges()
  //     .pipe(
  //       map(action => {
  //         const firebaseElement = action.payload.data() as FirebaseElement;
  //         if (firebaseElement === undefined) {
  //           // Element not found
  //           return;
  //         }
  //         return this.convertFirebaseElementToUser(firebaseElement);
  //       })
  //     )
  // }

  // private convertFirebaseElementToUser(firebaseElement: FirebaseElement): User {
  //   // Convert firebaseElement to binary
  //   const binary: Uint8Array = this.encodingService
  //     .decodeBase64StringToUint8Array(firebaseElement.proto);
  //   // Convert binary to data
  //   const user: User = User.deserializeBinary(binary);

  //   return user;
  // }
  private convertFirebaseElementToData(firebaseElement: FirebaseElement): Data {
    // Convert firebaseElement to binary
    const binary: Uint8Array = this.encodingService
      .decodeBase64StringToUint8Array(firebaseElement.proto);
    // Convert binary to data
    const data: Data = Data.deserializeBinary(binary);

    return data;
  }
}
