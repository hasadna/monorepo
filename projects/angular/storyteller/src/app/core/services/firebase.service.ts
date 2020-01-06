import { Injectable } from '@angular/core';
import { AngularFirestore, AngularFirestoreCollection } from '@angular/fire/firestore';
import { AngularFireStorage } from '@angular/fire/storage';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { randstr64 } from 'rndmjs';

import { Story, Moment, ReviewerConfig, Track } from '@/core/proto';
import { EncodingService } from 'common/services';
import { AuthService } from './auth.service';

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
    private authService: AuthService,
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

  createStory(story: Story): Observable<string> {
    story.setAuthor(this.authService.email);
    story.setId(randstr64(12));
    story.setStartedMs(Date.now());
    return new Observable(observer => {
      this.db.collection(`/storyteller/data/user/${this.authService.email}/story`)
        .doc(story.getId())
        .set({
          proto: this.encodingService.encodeUint8ArrayToBase64String(story.serializeBinary()),
        })
        .then(() => observer.next(story.getId()))
        .catch(() => observer.error());
    });
  }

  removeStory(story: Story): Observable<void> {
    return new Observable(observer => {
      this.db.collection(`/storyteller/data/user/${story.getAuthor()}/story`)
        .doc(story.getId())
        .delete()
        .then(() => observer.next())
        .catch(() => observer.error());
    });
  }

  addTrack(track: Track): Observable<string> {
    track.setId(randstr64(20));
    const email: string = this.authService.email;
    return new Observable(observer => {
      this.db.collection(`/storyteller/data/user/${email}/story/${track.getStoryId()}/track`)
        .doc(track.getId())
        .set({
          proto: this.encodingService.encodeUint8ArrayToBase64String(track.serializeBinary()),
        })
        .then(() => observer.next())
        .catch(() => observer.error());
    });
  }

  getTracks(storyId: string, email: string): Observable<Track[]> {
    return this.db.collection(`/storyteller/data/user/${email}/story/${storyId}/track`)
      .snapshotChanges()
      .pipe(
        map(action => action.map(a => {
          const firebaseElement = a.payload.doc.data() as FirebaseElement;
          return Track.deserializeBinary(this.getBinary(firebaseElement));
        })),
      );
  }
}
