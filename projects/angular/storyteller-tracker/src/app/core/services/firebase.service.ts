import { Injectable } from '@angular/core';
import { AngularFirestore } from '@angular/fire/firestore';
import { AngularFireStorage, AngularFireStorageReference } from '@angular/fire/storage';
import { Observable } from 'rxjs';
import { randCustomString, numerals } from 'rndmjs';

import { Screenshot, StoryList } from '@/core/proto';
import { EncodingService } from 'common/services';

@Injectable()
export class FirebaseService {
  constructor(
    private db: AngularFirestore,
    private encodingService: EncodingService,
    private storage: AngularFireStorage,
  ) { }

  uploadScreenshot(screenshot: Screenshot, mime: string): Observable<string> {
    const base64: string = screenshot.getScreenshot_asB64().split(',')[1];
    const fileRef: AngularFireStorageReference = this.storage.ref(screenshot.getFilename());
    return new Observable(observer => {
      const blob = new Blob(
        [this.encodingService.decodeBase64StringToUint8Array(base64)],
        { type: mime },
      );
      this.storage.upload(screenshot.getFilename(), blob)
        .then(() => fileRef.getDownloadURL().subscribe(url => observer.next(url)))
        .catch(err => observer.error());
    });
  }

  addStoryList(storyList: StoryList, email: string, timestamp: number): Observable<string> {
    const binary: Uint8Array = storyList.serializeBinary();
    const id: string = timestamp.toString() + randCustomString(numerals, 4);
    return new Observable(observer => {
      this.db.collection(`/storyteller/data/user/${email}/story`)
        .doc(id)
        .set({
          proto: this.encodingService.encodeUint8ArrayToBase64String(binary),
        })
        .then(() => observer.next(id))
        .catch(() => observer.error());
    });
  }
}
