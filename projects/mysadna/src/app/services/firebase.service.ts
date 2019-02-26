import { Injectable } from '@angular/core';
import { AngularFirestore, AngularFirestoreCollection } from 'angularfire2/firestore';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Project, User } from '@/proto';
import { EncodingService } from './encoding.service';

interface FirebaseElement {
  proto: string;
}

@Injectable()
export class FirebaseService {
  private userList: AngularFirestoreCollection<FirebaseElement>;
  private projectList: AngularFirestoreCollection<FirebaseElement>;

  constructor(
    private db: AngularFirestore,
    private encodingService: EncodingService
  ) {
    this.projectList = this.db.collection('protobin/data/project-list');
    this.userList = this.db.collection('protobin/data/user-list');
  }

  getUserList(): Observable<User[]> {
    return this.userList.snapshotChanges().pipe(
      map(action => action.map(a => {
        const firebaseElement: FirebaseElement = a.payload.doc.data();
        return User.deserializeBinary(this.getBinary(firebaseElement));
      }))
    );
  }

  getProjectList(): Observable<Project[]> {
    return this.projectList.snapshotChanges().pipe(
      map(action => action.map(a => {
        const firebaseElement: FirebaseElement = a.payload.doc.data();
        return Project.deserializeBinary(this.getBinary(firebaseElement));
      }))
    );
  }

  // Converts firebaseElement to binary
  private getBinary(firebaseElement: FirebaseElement): Uint8Array {
    const binary: Uint8Array = this.encodingService.decodeBase64StringToUint8Array(
      firebaseElement.proto
    );
    return binary;
  }
}
