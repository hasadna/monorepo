import { Component } from '@angular/core';

import { StoryList, Story } from '@/core/proto';
import { EncodingService, FirebaseService } from '@/core/services';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  storylist: StoryList[];


  constructor(
    private firebaseService: FirebaseService,
    private encodingService: EncodingService,
  ) {
    if (firebaseService.isOnline) {
      this.load();
    } else {
      this.firebaseService.anonymousLogin().then(() => {
        this.load();
      });
    }
  }

  load(): void {
    this.firebaseService.getstorylistAll().subscribe(storylist => {
      this.storylist = storylist;

    });
  }
}
