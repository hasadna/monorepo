import { Component } from '@angular/core';

import { EncodingService, FirebaseService } from '@/services';
import { Data } from '@/proto';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  data: Data;

  constructor(
    private encodingService: EncodingService,
    private firebaseService: FirebaseService,
  ) {
    this.firebaseService.getData().subscribe(data => {
      this.data = data;
    });
  }
}
