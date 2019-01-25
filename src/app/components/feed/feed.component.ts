import { Component, OnInit } from '@angular/core';
import { StoryList, Story } from '@/core/proto';
import { EncodingService, FirebaseService } from '@/core/services';
@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.scss']
})
export class FeedComponent implements OnInit {
  storylist: StoryList[];
  constructor(
    // private encodingService: EncodingService,
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

  ngOnInit() {
  }

}


