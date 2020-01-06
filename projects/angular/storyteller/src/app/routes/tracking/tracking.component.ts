import { Component, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';

import { Story, Track } from '@/core/proto';
import {
  LoadingService,
  UserService,
  FirebaseService,
  AuthService,
  Timer,
} from '@/core/services';

interface TrackRow {
  startMs: number;
  endMs: number;
  totalTime: string;
  isDayCrossed: boolean; // If date when track is started doesn't equal date when track is ended
}

// If two tracks have pause between themselves less that the interval, they are shown as one.
const MAX_PAUSE_INTERVAL: number = 5 * 60 * 1000; // 5 min

@Component({
  selector: 'page-tracking',
  templateUrl: './tracking.component.html',
  styleUrls: ['./tracking.component.scss'],
})
export class TrackingComponent implements OnDestroy {
  selectedStory: Story;
  trackRows: TrackRow[] = [];
  storyDurationTime: string;
  trackSub = new Subscription();
  tracks: Track[];
  isTrackingLoading: boolean = false;
  // Email of owner of the tracking
  email: string;
  radioLabels: string[] = ['Today', 'This week', 'This month', 'All'];
  selectedRangeIndex: number = 3;

  constructor(
    public loadingService: LoadingService,
    public userService: UserService,
    private firebaseService: FirebaseService,
    private authService: AuthService,
    private activatedRoute: ActivatedRoute,
  ) {
    this.activatedRoute.queryParams.subscribe(params => {
      if (params.email && params.email !== this.authService.email) {
        this.email = params.email;
      } else {
        this.email = this.authService.email;
      }
      this.loadingService.isLoading = false;
    });
  }

  storySelected(story: Story): void {
    this.isTrackingLoading = true;
    this.selectedStory = story;
    this.trackSub.unsubscribe();
    this.trackSub = this.firebaseService.getTracks(story.getId(), this.email)
      .subscribe(tracks => {
        this.tracks = tracks;
        this.applyTracks(tracks);
      });
  }

  applyTracks(tracks: Track[]): void {
    tracks.sort((a, b) => a.getEndedMs() - b.getEndedMs());
    tracks = tracks.filter(track => this.isInRange(track.getStartedMs()));

    // Tracks are saved each minute automatically.
    // To not show 9000 short tracks, combine the several tracks to one.
    this.trackRows = [];
    let startMs;
    let storyDurationMs: number = 0;
    tracks.forEach((track, i) => {
      const trackDurationMs: number = track.getEndedMs() - track.getStartedMs();
      storyDurationMs += trackDurationMs;

      const nextTrack: Track = tracks[i + 1];
      if (!startMs) {
        startMs = track.getStartedMs();
      }
      if (nextTrack && nextTrack.getStartedMs() - track.getEndedMs() < MAX_PAUSE_INTERVAL) {
        return;
      }
      this.addTrack(startMs, track.getEndedMs());
      startMs = undefined;
    });
    this.storyDurationTime = Timer.timestampToTime(storyDurationMs, false);
    this.trackRows.reverse();
    this.isTrackingLoading = false;
  }

  addTrack(startMs: number, endMs: number): void {
    const datePipe = new DatePipe('en-US');
    this.trackRows.push({
      startMs: startMs,
      endMs: endMs,
      totalTime: Timer.timestampToTime(endMs - startMs, false),
      isDayCrossed: datePipe.transform(startMs, 'MMM d') !== datePipe.transform(endMs, 'MMM d'),
    });
  }

  createNewStory(story: Story): void {
    this.isTrackingLoading = true;
    this.firebaseService.createStory(story).subscribe(() => {
      this.storySelected(story);
    });
  }

  selectRange(index: number): void {
    this.selectedRangeIndex = index;
    if (this.tracks) {
      this.applyTracks(this.tracks);
    }
  }

  getRangeMs(): number {
    const day: number = 24 * 60 * 60 * 1000;
    const week: number = 7 * day;
    const month: number = 30 * week;
    switch (this.selectedRangeIndex) {
      case 0: return day;
      case 1: return week;
      case 2: return month;
    }
    return week;
  }

  isInRange(ms: number): boolean {
    switch (this.selectedRangeIndex) {
      case 0: return this.isThisDay(ms);
      case 1: return this.isThisWeek(ms);
      case 2: return this.isThisMonth(ms);
      case 3: return true;
      default: throw new Error('Invalid range');
    }
  }

  isThisDay(ms: number): boolean {
    const currentDate = new Date(Date.now());
    const trackDate = new Date(ms);
    return true &&
      currentDate.getDate() === trackDate.getDate() &&
      currentDate.getMonth() === trackDate.getMonth() &&
      currentDate.getFullYear() === trackDate.getFullYear();
  }

  isThisWeek(ms: number): boolean {
    const now = new Date(Date.now());
    now.setHours(0, 0, 0, 0);
    const weekStart: number = (new Date(now.setDate(now.getDate() - now.getDay() + 1))).getTime();
    const weekEnd: number = weekStart + 7 * 24 * 60 * 60 * 1000;
    return ms >= weekStart && ms <= weekEnd;
  }

  isThisMonth(ms: number): boolean {
    const currentDate = new Date(Date.now());
    const trackDate = new Date(ms);
    return true &&
      currentDate.getMonth() === trackDate.getMonth() &&
      currentDate.getFullYear() === trackDate.getFullYear();
  }

  ngOnDestroy() {
    this.trackSub.unsubscribe();
  }
}
