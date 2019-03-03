import { Injectable } from '@angular/core';
import { AngularFirestore, AngularFirestoreCollection } from 'angularfire2/firestore';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { StoryList, Screenshot } from '@/core/proto';
import { EncodingService } from './encoding.service';
import { AngularFireAuth } from 'angularfire2/auth';
import * as firebase from 'firebase/app';
interface FirebaseElement {
  proto: string;
}

@Injectable()
export class FirebaseService {
  isOnline: boolean;
  private stories: AngularFirestoreCollection<FirebaseElement>;
  private screenshots: AngularFirestoreCollection<FirebaseElement>;

  constructor(
    private db: AngularFirestore,
    private encodingService: EncodingService,
    private angularFireAuth: AngularFireAuth,
  ) {
    this.angularFireAuth.authState.subscribe(userData => {
      this.isOnline = !!userData;
    });
    this.stories =
      this.db.collection(`/storyteller/data/user/valerii.fedorenko.ua@gmail.com/story`);
    this.screenshots =
      this.db.collection('/storyteller/data/user/valerii.fedorenko.ua@gmail.com/screenshot');
  }
  getStorylistAll(): Observable<StoryList[]> {
    return this.stories.snapshotChanges().pipe(
      map(action => action.map(a => {
        const firebaseElement = a.payload.doc.data() as FirebaseElement;

        if (firebaseElement === undefined) {
          // Element not found
          return;
        }
        return StoryList.deserializeBinary(this.getBinary(firebaseElement));
      })));
  }
  getScreenshotAll(): Observable<Screenshot[]> {
    return this.screenshots.snapshotChanges().pipe(
      map(action => action.map(a => {
        const firebaseElement = a.payload.doc.data() as FirebaseElement;

        if (firebaseElement === undefined) {
          // Element not found
          return;
        }
        return Screenshot.deserializeBinary(this.getBinary(firebaseElement));
      })));
  }
  // Converts firebaseElement to binary
  private getBinary(firebaseElement: FirebaseElement): Uint8Array {
    const binary: Uint8Array = this.encodingService.decodeBase64StringToUint8Array(
      firebaseElement.proto
    );
    return binary;
  }
  anonymousLogin(): Promise<any> {
    return this.angularFireAuth.auth.signInAnonymously();
  }
}
