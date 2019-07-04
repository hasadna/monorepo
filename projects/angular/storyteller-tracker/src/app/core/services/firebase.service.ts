import { Injectable } from '@angular/core';
import { AngularFirestore } from '@angular/fire/firestore';
import { AngularFireStorage, AngularFireStorageReference } from '@angular/fire/storage';
import { Observable } from 'rxjs';
import { randstr64 } from 'rndmjs';
import { map } from 'rxjs/operators';

import { Story, Track, Moment } from '@/core/proto';
import { Screenshot, FirebaseElement } from '@/core/interfaces';
import { EncodingService } from 'common/services';
import { AuthService } from './auth.service';

@Injectable()
export class FirebaseService {
  constructor(
    private db: AngularFirestore,
    private encodingService: EncodingService,
    private storage: AngularFireStorage,
    private authService: AuthService,
  ) { }

  uploadScreenshot(screenshot: Screenshot): Observable<string> {
    const fileRef: AngularFireStorageReference = this.storage.ref(screenshot.filename);
    return new Observable(observer => {
      const blob = new Blob(
        [this.encodingService.decodeBase64StringToUint8Array(screenshot.base64)],
        { type: screenshot.mime },
      );
      this.storage.upload(screenshot.filename, blob)
        .then(() => fileRef.getDownloadURL().subscribe(url => observer.next(url)))
        .catch(err => observer.error());
    });
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

  updateStory(story: Story): Observable<void> {
    return new Observable(observer => {
      this.db.collection(`/storyteller/data/user/${this.authService.email}/story`)
        .doc(story.getId())
        .update({
          proto: this.encodingService.encodeUint8ArrayToBase64String(story.serializeBinary()),
        })
        .then(() => observer.next())
        .catch(() => observer.error());
    });
  }

  addTrack(track: Track): Observable<string> {
    track.setId(randstr64(20));
    return new Observable(observer => {
      this.db.collection(`/storyteller/data/user/${this.authService.email}/track`)
      .doc(track.getId())
        .set({
          proto: this.encodingService.encodeUint8ArrayToBase64String(track.serializeBinary()),
        })
        .then(() => observer.next())
        .catch(() => observer.error());
    });
  }

  addMoment(moment: Moment): Observable<string> {
    moment.setId(randstr64(16));
    return new Observable(observer => {
      this.db.collection(`/storyteller/data/user/${this.authService.email}/moment`)
        .doc(moment.getId())
        .set({
          proto: this.encodingService.encodeUint8ArrayToBase64String(moment.serializeBinary()),
        })
        .then(() => observer.next())
        .catch(() => observer.error());
    });
  }

  getStory(id: string): Observable<Story> {
    return this.db.collection(`/storyteller/data/user/${this.authService.email}/story`)
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

  getStoryList(): Observable<Story[]> {
    return this.db.collection(`/storyteller/data/user/${this.authService.email}/story`)
      .snapshotChanges()
      .pipe(
        map(action => action.map(a => {
          const firebaseElement = a.payload.doc.data() as FirebaseElement;
          return Story.deserializeBinary(this.getBinary(firebaseElement));
        })),
      );
  }

  getTracks(): Observable<Track[]> {
    return this.db.collection(`/storyteller/data/user/${this.authService.email}/track`)
      .snapshotChanges()
      .pipe(
        map(action => action.map(a => {
          const firebaseElement = a.payload.doc.data() as FirebaseElement;
          return Track.deserializeBinary(this.getBinary(firebaseElement));
        })),
      );
  }

  getMoments(): Observable<Moment[]> {
    return this.db.collection(`/storyteller/data/user/${this.authService.email}/moment`)
      .snapshotChanges()
      .pipe(
        map(action => action.map(a => {
          const firebaseElement = a.payload.doc.data() as FirebaseElement;
          return Moment.deserializeBinary(this.getBinary(firebaseElement));
        })),
      );
  }

  getScreenshotURL(filename: string): Observable<string> {
    return new Observable(observer => {
      this.storage
        .ref('storyteller/' + filename)
        .getDownloadURL()
        .subscribe((url: string) => {
          observer.next(url);
        }, () => {
          observer.error();
        });
    });
  }

  // Converts FirebaseElement to binary
  private getBinary(firebaseElement: FirebaseElement): Uint8Array {
    const binary: Uint8Array = this.encodingService.decodeBase64StringToUint8Array(
      firebaseElement.proto,
    );
    return binary;
  }
}
