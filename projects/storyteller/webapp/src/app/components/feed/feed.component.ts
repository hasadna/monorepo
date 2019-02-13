import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { StoryList, Story } from '@/core/proto';
import { EncodingService, FirebaseService } from '@/core/services';

import { from } from 'rxjs';
@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.scss']
})
export class FeedComponent implements OnInit {
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

  ngOnInit() {
  }

  scroll(id) {
    const el = document.getElementById(id);
    el.scrollIntoView({ behavior: 'smooth'});
  }
}





