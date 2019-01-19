import { Injectable } from '@angular/core';
import { AngularFirestore, AngularFirestoreCollection } from 'angularfire2/firestore';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { StoryList } from '@/core/proto';
import { EncodingService } from './encoding.service';
import { AngularFireAuth } from 'angularfire2/auth';
import * as firebase from 'firebase/app';
interface FirebaseElementStory {
  story: string;
}

@Injectable()
export class FirebaseService {
  isOnline: boolean;
  private protobin: AngularFirestoreCollection<FirebaseElementStory>;

  constructor(
    private db: AngularFirestore,
    private encodingService: EncodingService,
    private angularFireAuth: AngularFireAuth,
  ) {
      this.angularFireAuth.authState.subscribe(userData => {
      this.isOnline = !!userData;
      });
    this.protobin = this.db.collection('storyteller');
  }

  getstorylistAll(): Observable<StoryList[]> {
    // this.shirts = this.shirtCollection.snapshotChanges().pipe(
    //   map(actions => actions.map(a => {
    //     const data = a.payload.doc.data() as Shirt;
    //     const id = a.payload.doc.id;
    //     return { id, ...data };
    //   }))
    return this.protobin.snapshotChanges().pipe(
        map(action => action.map(a => {
          const firebaseElement = a.payload.doc.data() as FirebaseElementStory;

          if (firebaseElement === undefined) {
            // Element not found
            return;
          }
          console.log(firebaseElement['proto']);
          return this.convertFirebaseElementToStory(firebaseElement);
        })))
  }

  private convertFirebaseElementToStory(firebaseElement: FirebaseElementStory): StoryList {

    //Convert firebaseElement to binary
    const binary: Uint8Array = this.encodingService
      .decodeBase64StringToUint8Array(firebaseElement['proto']);

    // Convert binary to book
    const storylist: StoryList = StoryList.deserializeBinary(binary);
    return storylist;
  }
  anonymousLogin(): Promise<any> {
    return this.angularFireAuth.auth.signInAnonymously();
  }
}
