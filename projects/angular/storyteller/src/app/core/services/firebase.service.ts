import { Injectable } from '@angular/core';
import { AngularFirestore, AngularFirestoreCollection } from '@angular/fire/firestore';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Screenshot, StoryList, ReviewerConfig } from '@/core/proto';
import { EncodingService } from 'common/services';

interface FirebaseElement {
  proto: string;
}

@Injectable()
export class FirebaseService {
  private reviewerConfig: AngularFirestoreCollection<FirebaseElement>;

  constructor(
    private db: AngularFirestore,
    private encodingService: EncodingService,
  ) {
    this.reviewerConfig = this.db.collection('reviewer');
  }

  getStoryLists(
    collection: AngularFirestoreCollection<FirebaseElement>,
  ): Observable<StoryList[]> {
    return collection.snapshotChanges().pipe(
      map(action => action.map(a => {
        const firebaseElement = a.payload.doc.data() as FirebaseElement;
        return StoryList.deserializeBinary(this.getBinary(firebaseElement));
      })));
  }

  getScreenshots(
    collection: AngularFirestoreCollection<FirebaseElement>,
  ): Observable<Screenshot[]> {
    return collection.snapshotChanges().pipe(
      map(action => action.map(a => {
        const firebaseElement = a.payload.doc.data() as FirebaseElement;
        return Screenshot.deserializeBinary(this.getBinary(firebaseElement));
      })));
  }

  getUserStories(userEmail: string): Observable<StoryList[]> {
    let collection: AngularFirestoreCollection<FirebaseElement>;
    if (userEmail) {
      collection = this.db.collection(`/storyteller/data/user/${userEmail}/story`);
      return this.getStoryLists(collection);
    }
  }

  getUserScreenshot(userEmail: string): Observable<Screenshot[]> {
    let collection: AngularFirestoreCollection<FirebaseElement>;
    if (userEmail) {
      collection = this.db.collection(`/storyteller/data/user/${userEmail}/screenshot`);
      return this.getScreenshots(collection);
    }
  }

  // Converts firebaseElement to binary
  private getBinary(firebaseElement: FirebaseElement): Uint8Array {
    const binary: Uint8Array = this.encodingService.decodeBase64StringToUint8Array(
      firebaseElement.proto,
    );
    return binary;
  }

  // Gets users from Firebase
  getReviewerConfig(): Observable<ReviewerConfig> {
    return this.reviewerConfig
      .doc('config_binary')
      .snapshotChanges()
      .pipe(
        map(action => {
          const firebaseElement: FirebaseElement = action.payload.data() as FirebaseElement;
          return ReviewerConfig.deserializeBinary(this.getBinary(firebaseElement));
        }),
      );
  }
}
