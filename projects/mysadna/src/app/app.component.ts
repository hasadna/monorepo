import { Component } from '@angular/core';

import { EncodingService } from './services/encoding.service';
import { FirebaseService } from './services/firbase.service';
import { Data } from './proto';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'mysadna';
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
