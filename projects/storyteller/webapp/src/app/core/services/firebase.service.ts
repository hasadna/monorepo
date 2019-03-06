// A little bit of code formatting here too
import { Injectable } from '@angular/core';
import {
  AngularFirestore,
  AngularFirestoreCollection,
  AngularFirestoreDocument
} from 'angularfire2/firestore';
import { AngularFireAuth } from 'angularfire2/auth';
import * as firebase from 'firebase/app';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { StoryList, Screenshot } from '@/core/proto';
import { EncodingService } from './encoding.service';

interface FirebaseElement {
  proto: string;
}

interface UserData {
  users: string[];
}

@Injectable()
export class FirebaseService {
  isOnline: boolean;
  users: string[];
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
      if (this.isOnline) {
        this.loadusers();
      }
    });
    this.protobin_users = this.db.collection('/storyteller');

    this.protobin = this.db.collection(
      `/storyteller/data/user/valerii.fedorenko.ua@gmail.com/story`
    );
    this.protobin_screenshots = this.db.collection(
      '/storyteller/data/user/valerii.fedorenko.ua@gmail.com/screenshot'
    );
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

  getUsersAll(): Observable<UserData> {
    const userData: AngularFirestoreDocument<UserData> = this.protobin_users.doc('data');
    return userData.valueChanges();
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
    this.getUsersAll().subscribe(userData => {
      console.log(userData.users);
      this.users = userData.users;
    });
  }

  anonymousLogin(): Promise<any> {
    return this.angularFireAuth.auth.signInAnonymously();
  }
}
