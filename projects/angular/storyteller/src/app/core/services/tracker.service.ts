import { Injectable } from '@angular/core';
import { Observable, Subscription, zip } from 'rxjs';
import { first } from 'rxjs/operators';

import { Story, Track } from '@/core/proto';
import { AuthService } from './auth.service';
import { FirebaseService } from './firebase.service';
import { NotificationService } from './notification.service';
import { UserService } from './user.service';

export class Timer {
  sessionMs: number;
  savedMs: number;
  trackedMs: number;
  time: string;
  timeWithoutSeconds: string;

  constructor() {
    this.init();
  }

  init(): void {
    this.sessionMs = 0;
    this.savedMs = 0;
    this.trackedMs = 0;
    this.display();
  }

  save(): void {
    this.savedMs += this.sessionMs;
    this.sessionMs = 0;
    this.display();
  }

  display(ms: number = 0): void {
    this.sessionMs = ms;
    this.trackedMs = this.savedMs + this.sessionMs;
    this.time = Timer.timestampToTime(this.trackedMs);
    this.timeWithoutSeconds = Timer.timestampToTime(this.trackedMs, false);
  }

  // 1561209830000 -> 1:23:50
  static timestampToTime(ms: number, isSecond: boolean = true): string {
    function getZero(num: number): string {
      num = Math.floor(num);
      return ('0' + num).slice(-2);
    }

    const date: Date = new Date(ms);
    const hours: number = Math.floor(ms / (1000 * 60 * 60));
    const minutes: string = getZero(date.getMinutes());
    let time: string = hours + ':' + minutes;
    if (isSecond) {
      time += ':' + getZero(date.getSeconds());
    }
    return time;
  }
}

@Injectable()
export class TrackerService {
  isTracking: boolean = false;
  isStoryTracksLoading: boolean = false;
  isStoriesTracksLoading: boolean = true;
  selectedStory: Story;
  sessionTimer = new Timer();
  storyTimer = new Timer();
  totalTimer = new Timer();
  startMs: number = 0;
  endMs: number = 0;
  sendedMs: number = 0;
  timer: number;
  autosend: number;
  sinceTime: string = '0:00';
  storySub = new Subscription();
  zipSub = new Subscription();
  storyList: Story[] = [];

  constructor(
    private authService: AuthService,
    private firebaseService: FirebaseService,
    private notificationService: NotificationService,
    private userService: UserService,
  ) {
    this.authService.onlineChanges.subscribe(isOnline => {
      if (isOnline) {
        // Get stories
        this.storySub = this.userService.onloadStoryList().subscribe(storyList => {
          this.storyList = storyList;
          this.displayTotalTimer();
        });
      } else {
        this.stop();
        this.storySub.unsubscribe();
        this.zipSub.unsubscribe();
      }
    });
  }

  // Counts and displays tracking time from all stories since selected time
  displayTotalTimer(): void {
    this.isStoriesTracksLoading = true;
    const observables: Observable<number>[] = this.storyList.map(
      story => this.getStoryTracking(story.getId()),
    );
    if (observables.length > 0) {
      this.zipSub = zip(...observables).subscribe(data => {
        this.totalTimer.init();
        for (const ms of data) {
          this.totalTimer.savedMs += ms;
        }
        this.totalTimer.display();
        this.isStoriesTracksLoading = false;
      });
    } else {
      this.totalTimer.display();
      this.isStoriesTracksLoading = false;
    }
  }

  init(): void {
    this.sessionTimer.init();
    this.storyTimer.init();
  }

  // Update all timers
  displayMs(ms: number = 0) {
    this.sessionTimer.display(ms);
    this.storyTimer.display(ms);
    this.totalTimer.display(ms);
  }

  start(): void {
    this.isTracking = true;
  }

  stop(): void {
    if (this.isTracking) {
      window.clearInterval(this.timer);
      window.clearInterval(this.autosend);
      this.isTracking = false;
    }
  }

  // When button "start" is pressed
  pressStart(): void {
    this.startMs = Date.now();
    this.sendedMs = this.startMs;
    const interval = () => {
      this.endMs = Date.now();
      this.displayMs(this.endMs - this.startMs);
    };
    this.timer = window.setInterval(() => { interval(); }, 1000);
    this.start();
    interval();
    const autoSaveIntervalMin: number = 1; // Save track each minute
    this.autosend = window.setInterval(() => {
      if (this.isTracking) {
        this.sendTrack().subscribe();
      }
    }, autoSaveIntervalMin * 60 * 1000);
  }

  // When button "stop" is pressed
  pressStop(): void {
    this.sendTrack().subscribe(() => {
      this.notificationService.success('Track saved');
    });
    this.displayMs(this.endMs - this.startMs);
    this.storyTimer.save();
    this.totalTimer.save();
    this.stop();
  }

  selectStory(story: Story): void {
    this.isStoryTracksLoading = true;
    this.stop();
    this.init();
    this.selectedStory = story;
    this.getStoryTracking(this.selectedStory.getId()).subscribe(ms => {
      this.storyTimer.init();
      this.storyTimer.savedMs = ms;
      this.storyTimer.display();
      this.isStoryTracksLoading = false;
    });
  }

  private sendTrack(): Observable<void> {
    return new Observable(observer => {
      const now: number = Date.now();
      const trackedInterval: number = now - this.sendedMs;
      if (trackedInterval > 1000) {
        // Save only the tracks, which are more than 1 sec
        const track = new Track();
        track.setStoryId(this.selectedStory.getId());
        track.setStartedMs(this.sendedMs);
        track.setEndedMs(now);
        this.firebaseService.addTrack(track).subscribe(() => {
          observer.next();
        });
        this.sendedMs = now;
      }
    });
  }

  private getStoryTracking(storyId: string): Observable<number> {
    return new Observable(observer => {
      this.firebaseService.getTracks(storyId)
        .pipe(first())
        .subscribe(tracks => {
          const sinceTracks: Track[] = tracks.filter(track => {
            // Ignore all tracks, which was later than sinceTime.
            // Convert "14:30" sinceTime to timestamp
            const dateNow = new Date(Date.now());
            const sinceTrackMs: number = dateNow.getTime() - track.getStartedMs();
            const sinceHM: number[] = this.sinceTime.split(':').map(t => parseInt(t));
            const sinceHours: number = sinceHM[0];
            const sinceMinutes: number = sinceHM[1];
            let limitHours: number = dateNow.getHours() - sinceHours;
            if (limitHours < 0) {
              limitHours += 24;
            }
            let limitMinutes: number = dateNow.getMinutes() - sinceMinutes;
            if (limitMinutes < 0) {
              limitMinutes += 60;
            }
            limitHours += limitMinutes / 60;
            const limitMs: number = limitHours * 60 * 60 * 1000;
            return (sinceTrackMs < limitMs);
          });
          let ms: number = 0;
          for (const track of sinceTracks) {
            ms += track.getEndedMs() - track.getStartedMs();
          }
          observer.next(ms);
        });
    });
  }
}
