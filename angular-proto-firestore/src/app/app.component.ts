import { Component } from '@angular/core';

import { StoryItem } from '@/core/proto';
import { EncodingService, FirebaseService } from '@/core/services';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  story: StoryItem;

  constructor(
    private encodingService: EncodingService,
    private firebaseService: FirebaseService,
  ) {
    this.firebaseService.getStory().subscribe(story=> {
      this.story = story;
    });
  }
}
