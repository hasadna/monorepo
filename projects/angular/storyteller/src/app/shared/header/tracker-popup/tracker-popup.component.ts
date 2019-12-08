import { Component } from '@angular/core';
import { FormControl } from '@angular/forms';
import { CookieService } from 'ngx-cookie-service';

import { Story } from '@/core/proto';
import {
  TrackerService,
  UserService,
  FirebaseService,
} from '@/core/services';
import { ClockService } from './clock.service';

@Component({
  selector: 'tracker-popup',
  templateUrl: './tracker-popup.component.html',
  styleUrls: ['./tracker-popup.component.scss'],
  providers: [ClockService],
})
export class TrackerPopupComponent {
  isHidden: boolean = true;
  clockList: string[];
  sinceTime = new FormControl();

  constructor(
    public userService: UserService,
    public trackerService: TrackerService,
    private cookieService: CookieService,
    private clockService: ClockService,
    private firebaseService: FirebaseService,
  ) {
    this.initSinceTime();
    this.clockList = this.clockService.generateClockList();
  }

  initSinceTime(): void {
    // Get sinceTime from cookie
    let sinceTimeCookie: string = this.cookieService.get('sinceTime');
    if (!sinceTimeCookie) {
      sinceTimeCookie = '0:00';
    }
    this.sinceTime.setValue(sinceTimeCookie, { emitEvent: false });
    this.trackerService.sinceTime = sinceTimeCookie;

    // When sinceTime is changed
    this.sinceTime.valueChanges.subscribe((sinceTime: string) => {
      this.trackerService.sinceTime = sinceTime;
      this.cookieService.set('sinceTime', sinceTime, 3000); // Keep the cookie for 3000 days

      // Recount tracking
      this.trackerService.displayTotalTimer();
      if (this.trackerService.selectedStory) {
        this.trackerService.selectStory(this.trackerService.selectedStory);
      }
    });
  }

  storySelectValueChanges(): void {
    // Stop tracking and make the story selected in service
    this.trackerService.stop();
    this.trackerService.selectedStory = undefined;
  }

  storySelected(story: Story): void {
    this.trackerService.selectStory(story);
  }

  createNewStory(story: Story): void {
    this.firebaseService.createStory(story).subscribe(() => {
      this.trackerService.selectStory(story);
    });
  }

  open(): void {
    this.isHidden = false;
  }

  close(): void {
    this.isHidden = true;
  }
}
