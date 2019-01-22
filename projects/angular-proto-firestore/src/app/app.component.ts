import { Component } from '@angular/core';

import { Book } from '@/core/proto';
import { EncodingService, FirebaseService } from '@/core/services';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  book: Book;

  constructor(
    private encodingService: EncodingService,
    private firebaseService: FirebaseService,
  ) {
    this.firebaseService.getBook().subscribe(book => {
      this.book = book;
    });
  }
}
