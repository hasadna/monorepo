import { Injectable } from '@angular/core';
import { AngularFirestore, AngularFirestoreCollection } from 'angularfire2/firestore';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { User, Project, Contribution, Data } from '../proto';
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
    this.protobin = this.db.collection('protobin');
  }

  getData(): Observable<Data> {
    return this.protobin
      .doc('data')
      .snapshotChanges()
      .pipe(
        map(action => {
          const firebaseElement = action.payload.data() as FirebaseElement;
          if (firebaseElement === undefined) {
            // Element not found
            return;
          }
          return this.convertFirebaseElementToData(firebaseElement);
        })
      );
  }

  private convertFirebaseElementToData(firebaseElement: FirebaseElement): Data {
    // Convert firebaseElement to binary
    const binary: Uint8Array = this.encodingService
      .decodeBase64StringToUint8Array(firebaseElement.proto);
    // Convert binary to data
    const data: Data = Data.deserializeBinary(binary);

    return data;
  }
}
