import { Injectable } from '@angular/core';
import { AngularFirestore, AngularFirestoreCollection, AngularFirestoreDocument } from 'angularfire2/firestore';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { StoryList,Screenshot } from '@/core/proto';
import { EncodingService } from './encoding.service';
import { AngularFireAuth } from 'angularfire2/auth';
import * as firebase from 'firebase/app';
interface FirebaseElement {
  proto: string;
}

interface Users {
  users: string[];
}

@Injectable()
export class FirebaseService{
  isOnline: boolean;
  users: Users;
  private protobin: AngularFirestoreCollection<FirebaseElement>;
  private protobin_screenshots: AngularFirestoreCollection<FirebaseElement>;
  private protobin_users: AngularFirestoreCollection<FirebaseElement>;

  constructor(
    private db: AngularFirestore,
    private encodingService: EncodingService,
    private angularFireAuth: AngularFireAuth,
  ) {
      this.angularFireAuth.authState.subscribe(userData => {
      this.isOnline = !!userData;
      });
      this.protobin_users = this.db.collection('/storyteller');
      this.loadusers();
      console.log(this.users);
      // this.protobin = this.db.collection(`/storyteller/data/user/${this.users.users[0]}/story`);
    this.protobin = this.db.collection(`/storyteller/data/user/valerii.fedorenko.ua@gmail.com/story`);
    this.protobin_screenshots = this.db.collection('/storyteller/data/user/valerii.fedorenko.ua@gmail.com/screenshot');

  }

  getstorylistAll(): Observable<StoryList[]> {
    return this.protobin.snapshotChanges().pipe(
        map(action => action.map(a => {
          const firebaseElement = a.payload.doc.data() as FirebaseElement;

          if (firebaseElement === undefined) {
            // Element not found
            return;
          }
          return this.convertFirebaseElementToStory(firebaseElement);
        })));
  }
  getscreenshotAll(): Observable<Screenshot[]> {
    return this.protobin_screenshots.snapshotChanges().pipe(
        map(action => action.map(a => {
          const firebaseElement = a.payload.doc.data() as FirebaseElement;

          if (firebaseElement === undefined) {
            // Element not found
            return;
          }
          return this.convertFirebaseElementToScreenshot(firebaseElement);
        })));
  }

  getUsersAll(): Observable<string[]> {
    const users: AngularFirestoreDocument<string[]> = this.protobin_users.doc('data');
    return users.valueChanges();
  }

  private convertFirebaseElementToStory(firebaseElement: FirebaseElement): StoryList {

    // Convert firebaseElement to binary
    const binary: Uint8Array = this.encodingService
      .decodeBase64StringToUint8Array(firebaseElement.proto);

    // Convert binary to storylist
    const storylist: StoryList = StoryList.deserializeBinary(binary);
    return storylist;
  }
  
  private convertFirebaseElementToScreenshot(firebaseElement: FirebaseElement): Screenshot {
    // Convert firebaseElement to binary
    const binary: Uint8Array = this.encodingService
      .decodeBase64StringToUint8Array(firebaseElement.proto);

    // Convert binary to Screenshot
    const screenshot: Screenshot = Screenshot.deserializeBinary(binary);
    return screenshot;
  }

  loadusers(): void {
    this.getUsersAll().subscribe(users => {
      console.log(users);
      this.users= users;

    });
  }


  anonymousLogin(): Promise<any> {
    return this.angularFireAuth.auth.signInAnonymously();
  }
}
