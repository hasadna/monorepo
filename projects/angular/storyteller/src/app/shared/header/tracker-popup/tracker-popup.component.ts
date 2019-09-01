import { Component, OnDestroy } from '@angular/core';
import { FormControl } from '@angular/forms';
import { Subscription } from 'rxjs';
import { CookieService } from 'ngx-cookie-service';

import { Story } from '@/core/proto';
import {
  HeaderService,
  TreackerService,
  UserService,
  FirebaseService,
  AuthService,
} from '@/core/services';
import { ClockService } from './clock.service';

@Component({
  selector: 'tracker-popup',
  templateUrl: './tracker-popup.component.html',
  styleUrls: ['./tracker-popup.component.scss'],
  providers: [ClockService],
})
export class TrackerPopupComponent implements OnDestroy {
  isHidden: boolean = true;
  storySelect = new FormControl();
  onelinerList: string[] = [];
  storySub = new Subscription();
  clockList: string[];
  sinceTime = new FormControl();

  constructor(
    public headerService: HeaderService,
    public userService: UserService,
    public treackerService: TreackerService,
    private firebaseService: FirebaseService,
    private authService: AuthService,
    private cookieService: CookieService,
    private clockService: ClockService,
  ) {
    this.initSinceTime();

    this.clockList = this.clockService.generateClockList();

    if (this.treackerService.selectedStory) {
      this.storySelect.setValue(
        this.treackerService.selectedStory.getOneliner(),
        { emitEvent: false },
      );
    }
    this.storySub = this.firebaseService.getStoryList(this.authService.email).subscribe(() => {
      this.updateOnelinerList();
      // Check if selected story has deleted
      if (this.treackerService.selectedStory) {
        const idList: string[] = this.userService.storyList.map(story => story.getId());
        if (!idList.includes(this.treackerService.selectedStory.getId())) {
          // Looks like selected story was deleted. Unselect it
          this.treackerService.selectedStory = undefined;
          this.treackerService.stop();
          this.storySelect.setValue('', { emitEvent: false });
          this.updateOnelinerList();
        }
      }
    });
    // When story is selected by UI
    this.storySelect.valueChanges.subscribe((oneliner: string) => {
      // Stop tracking and make the story selected in service
      this.treackerService.stop();
      this.updateOnelinerList();
      this.treackerService.selectedStory = undefined;
      if (oneliner) {
        for (const story of this.userService.storyList) {
          if (story.getOneliner() === oneliner) {
            this.treackerService.selectStory(story);
            break;
          }
        }
      }
    });
  }

  initSinceTime(): void {
    // Get sinceTime from cookie
    let sinceTimeCookie: string = this.cookieService.get('sinceTime');
    if (!sinceTimeCookie) {
      sinceTimeCookie = '0:00';
    }
    this.sinceTime.setValue(sinceTimeCookie, { emitEvent: false });
    this.treackerService.sinceTime = sinceTimeCookie;

    // When sinceTime is changed
    this.sinceTime.valueChanges.subscribe((sinceTime: string) => {
      this.treackerService.sinceTime = sinceTime;
      this.cookieService.set('sinceTime', sinceTime);

      // Recount tracking
      this.treackerService.displayTotalTimer();
      if (this.treackerService.selectedStory) {
        this.treackerService.selectStory(this.treackerService.selectedStory);
      }
    });
  }

  updateOnelinerList(): void {
    // Show last 10 options, which include user input
    let storyList: Story[] = this.userService.storyList.slice();
    storyList.sort((a, b) => {
      return Math.sign(b.getStartedMs() - a.getStartedMs());
    });
    if (this.storySelect.value) {
      storyList = storyList.filter(story =>
        story.getOneliner().toLowerCase().includes(this.storySelect.value.toLowerCase()),
      );
    }
    storyList = storyList.slice(0, Math.min(10, storyList.length));
    this.onelinerList = storyList.map(story => story.getOneliner());
  }

  createNewStory(): void {
    const story = new Story();
    story.setOneliner(this.storySelect.value);
    this.firebaseService.createStory(story).subscribe(() => {
      this.treackerService.selectStory(story);
      this.updateOnelinerList();
    });
  }

  open(): void {
    this.isHidden = false;
  }

  close(): void {
    this.isHidden = true;
  }

  ngOnDestroy() {
    this.storySub.unsubscribe();
  }
}
