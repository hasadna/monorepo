import { Injectable, OnInit } from '@angular/core';
import { AngularFirestore, AngularFirestoreCollection, AngularFirestoreDocument } from 'angularfire2/firestore';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { StoryList, Screenshot } from '@/core/proto';
import { EncodingService } from './encoding.service';
import { AngularFireAuth } from 'angularfire2/auth';
import * as firebase from 'firebase/app';
interface UserData {
  users: string[];
}
interface FirebaseElement {
  proto: string;
}

@Injectable()
export class FirebaseService {

  isOnline: boolean;
  users: string[];
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
  }
  getstorylistAll(
    collection: AngularFirestoreCollection<FirebaseElement>
  ): Observable<StoryList[]> {
    return collection.snapshotChanges().pipe(
      map(action => action.map(a => {
        const firebaseElement = a.payload.doc.data() as FirebaseElement;

        if (firebaseElement === undefined) {
          // Element not found
          return;
        }
        return StoryList.deserializeBinary(this.getBinary(firebaseElement));
      })));
  }
  getScreenshotAll(
    collection: AngularFirestoreCollection<FirebaseElement>
  ): Observable<Screenshot[]> {
    return collection.snapshotChanges().pipe(
      map(action => action.map(a => {
        const firebaseElement = a.payload.doc.data() as FirebaseElement;

        if (firebaseElement === undefined) {
          // Element not found
          return;
        }
        return Screenshot.deserializeBinary(this.getBinary(firebaseElement));
      })));
  }
  getUserStories(user: string): Observable<StoryList[]> {
    let collection: AngularFirestoreCollection<FirebaseElement>;
    if (user) {
      collection = this.db.collection(`/storyteller/data/user/${user}/story`);
      return this.getstorylistAll(collection);
    }
  }
  getUserScreenshot(user: string): Observable<Screenshot[]> {
    let collection: AngularFirestoreCollection<FirebaseElement>;
    if (user) {
      const firstUser: string = user;
      collection = this.db.collection(`/storyteller/data/user/${firstUser}/screenshot`);
      return this.getScreenshotAll(collection);
    }
  }
  // Converts firebaseElement to binary
  private getBinary(firebaseElement: FirebaseElement): Uint8Array {
    const binary: Uint8Array = this.encodingService.decodeBase64StringToUint8Array(
      firebaseElement.proto
    );
    return binary;
  }
  // Getting users from Firebase
  getUsersAll(): Observable<UserData> {
    const userData: AngularFirestoreDocument<UserData> = this.protobin_users.doc('data');
    return userData.valueChanges();
  }
  anonymousLogin(): Promise<any> {
    return this.angularFireAuth.auth.signInAnonymously();
  }
}

