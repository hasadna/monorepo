import { Injectable } from '@angular/core';
import { AngularFirestore, AngularFirestoreCollection } from '@angular/fire/firestore';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Book } from '@/core/proto';
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

  getBook(): Observable<Book> {
    return this.protobin
      .doc('book')
      .snapshotChanges()
      .pipe(
        map(action => {
          const firebaseElement = action.payload.data() as FirebaseElement;
          if (firebaseElement === undefined) {
            // Element not found
            return;
          }
          return this.convertFirebaseElementToBook(firebaseElement);
        })
      );
  }

  private convertFirebaseElementToBook(firebaseElement: FirebaseElement): Book {
    // Convert firebaseElement to binary
    const binary: Uint8Array = this.encodingService
      .decodeBase64StringToUint8Array(firebaseElement.proto);
    // Convert binary to book
    const book: Book = Book.deserializeBinary(binary);

    return book;
  }
}
