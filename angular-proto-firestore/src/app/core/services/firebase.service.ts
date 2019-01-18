import { Injectable } from '@angular/core';
import { AngularFirestore, AngularFirestoreCollection } from 'angularfire2/firestore';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { StoryItem } from '@/core/proto';
import { EncodingService } from './encoding.service';

interface FirebaseElement {
  proto: string;
}

@Injectable()
export class FirebaseService {
  private protobin: AngularFirestoreCollection<FirebaseElement>;

  constructor(
    private db: AngularFirestore,
    private encodingService: EncodingService,
  ) {
    this.protobin = this.db.collection('storyteller');
  }

  getStory(): Observable<StoryItem> {
    return this.protobin
      .doc('Story')
      .snapshotChanges()
      .pipe(
        map(action => {
          const firebaseElement = action.payload.data() as FirebaseElement;
          if (firebaseElement === undefined) {
            // Element not found
            return;
          }
          return this.convertFirebaseElementToStory(firebaseElement);
        })
      );
  }

  private convertFirebaseElementToStory(firebaseElement: FirebaseElement): StoryItem {
    // Convert firebaseElement to binary
    const binary: Uint8Array = this.encodingService
      .decodeBase64StringToUint8Array(firebaseElement.proto);
    // Convert binary to book
    const story: StoryItem = StoryItem.deserializeBinary(binary);

    return story;
  }
}
