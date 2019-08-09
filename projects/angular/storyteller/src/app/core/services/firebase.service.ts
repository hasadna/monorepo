import { Injectable } from '@angular/core';
import { AngularFirestore, AngularFirestoreCollection } from '@angular/fire/firestore';
import { AngularFireStorage } from '@angular/fire/storage';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Story, Moment, ReviewerConfig } from '@/core/proto';
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
    private storage: AngularFireStorage,
  ) {
    this.reviewerConfig = this.db.collection('reviewer');
  }

  getStoryList(email: string): Observable<Story[]> {
    return this.db.collection(`/storyteller/data/user/${email}/story`)
      .snapshotChanges()
      .pipe(
        map(action => action.map(a => {
          const firebaseElement = a.payload.doc.data() as FirebaseElement;
          return Story.deserializeBinary(this.getBinary(firebaseElement));
        })),
      );
  }

  getStory(id: string, email: string): Observable<Story> {
    return this.db.collection(`/storyteller/data/user/${email}/story`)
      .doc(id)
      .snapshotChanges()
      .pipe(
        map(action => {
          const firebaseElement = action.payload.data() as FirebaseElement;
          if (!firebaseElement) {
            return;
          } else {
            return Story.deserializeBinary(this.getBinary(firebaseElement));
          }
        }),
      );
  }

  getMoments(email: string, storyId: string): Observable<Moment[]> {
    return this.db.collection(`/storyteller/data/user/${email}/story/${storyId}/moment`)
      .snapshotChanges()
      .pipe(
        map(action => action.map(a => {
          const firebaseElement = a.payload.doc.data() as FirebaseElement;
          return Moment.deserializeBinary(this.getBinary(firebaseElement));
        })),
      );
  }

  getMoment(id: string, email: string, storyId: string): Observable<Moment> {
    return this.db.collection(`/storyteller/data/user/${email}/story/${storyId}/moment`)
      .doc(id)
      .snapshotChanges()
      .pipe(
        map(action => {
          const firebaseElement = action.payload.data() as FirebaseElement;
          if (!firebaseElement) {
            return;
          } else {
            return Moment.deserializeBinary(this.getBinary(firebaseElement));
          }
        }),
      );
  }

  // Converts firebaseElement to binary
  private getBinary(firebaseElement: FirebaseElement): Uint8Array {
    const binary: Uint8Array = this.encodingService.decodeBase64StringToUint8Array(
      firebaseElement.proto,
    );
    return binary;
  }

  getReviewerConfig(): Observable<ReviewerConfig> {
    return this.reviewerConfig
      .doc('config_binary')
      .snapshotChanges()
      .pipe(
        map(action => {
          const firebaseElement = action.payload.data() as FirebaseElement;
          return ReviewerConfig.deserializeBinary(this.getBinary(firebaseElement));
        }),
      );
  }

  getScreenshotURL(filename: string): Observable<string> {
    return this.storage
      .ref('storyteller/' + filename)
      .getDownloadURL();
  }
}
