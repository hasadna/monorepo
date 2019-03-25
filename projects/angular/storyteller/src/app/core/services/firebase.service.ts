import { ReviewerConfig, Screenshot, StoryList } from '@/core/proto';
import { Injectable } from '@angular/core';
import { AngularFireAuth } from '@angular/fire/auth';
import { AngularFirestore, AngularFirestoreCollection } from '@angular/fire/firestore';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { EncodingService } from 'common/services';

interface FirebaseElement {
  proto: string;
}

@Injectable()
export class FirebaseService {
  isOnline: boolean;
  private reviewerConfig: AngularFirestoreCollection<FirebaseElement>;

  constructor(
    private db: AngularFirestore,
    private encodingService: EncodingService,
    private angularFireAuth: AngularFireAuth,
  ) {
    this.angularFireAuth.authState.subscribe(userData => {
      this.isOnline = !!userData;
    });
    this.reviewerConfig = this.db.collection('reviewer');
  }

  getStoryListAll(
    collection: AngularFirestoreCollection<FirebaseElement>,
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
    collection: AngularFirestoreCollection<FirebaseElement>,
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
      return this.getStoryListAll(collection);
    }
  }

  getUserScreenshot(user: string): Observable<Screenshot[]> {
    let collection: AngularFirestoreCollection<FirebaseElement>;
    if (user) {
      collection = this.db.collection(`/storyteller/data/user/${user}/screenshot`);
      return this.getScreenshotAll(collection);
    }
  }

  // Converts firebaseElement to binary
  private getBinary(firebaseElement: FirebaseElement): Uint8Array {
    const binary: Uint8Array = this.encodingService.decodeBase64StringToUint8Array(
      firebaseElement.proto,
    );
    return binary;
  }

  // Getting users from Firebase
  getReviewerConfig(): Observable<ReviewerConfig> {
    return this.reviewerConfig
      .doc('config_binary')
      .snapshotChanges()
      .pipe(
        map(action => {
          const firebaseElement: FirebaseElement = action.payload.data() as FirebaseElement;
          if (firebaseElement === undefined) {
            // Element not found
            return;
          }
          return ReviewerConfig.deserializeBinary(this.getBinary(firebaseElement));
        }),
      );
  }

  anonymousLogin(): Promise<any> {
    return this.angularFireAuth.auth.signInAnonymously();
  }
}
