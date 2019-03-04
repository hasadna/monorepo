import { Injectable } from '@angular/core';
import { AngularFirestore, AngularFirestoreCollection } from 'angularfire2/firestore';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { ReviewerConfig } from '@/proto';
import { EncodingService } from './encoding.service';

interface FirebaseElement {
  proto: string;
}

@Injectable()
export class FirebaseService {
  private dataSource: AngularFirestoreCollection<FirebaseElement>;

  constructor(
    private db: AngularFirestore,
    private encodingService: EncodingService
  ) {
      this.dataSource = this.db.collection('reviewer');
  }

  getReviewerConfig(): Observable<ReviewerConfig> {
    return this.dataSource
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
        })
      );
  }

  // Converts firebaseElement to binary
  private getBinary(firebaseElement: FirebaseElement): Uint8Array {
    const binary: Uint8Array = this.encodingService.decodeBase64StringToUint8Array(
      firebaseElement.proto
    );
    return binary;
  }
}
