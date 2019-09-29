import { Component, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { DatePipe } from '@angular/common';

import { Story, Track } from '@/core/proto';
import {
  LoadingService,
  UserService,
  FirebaseService,
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
  isTrackingLoading: boolean = false;

  constructor(
    public loadingService: LoadingService,
    public userService: UserService,
    private firebaseService: FirebaseService,
  ) {
    this.loadingService.isLoading = false;
  }

  storySelected(story: Story): void {
    this.isTrackingLoading = true;
    this.trackSub = this.firebaseService.getTracks(story.getId()).subscribe(tracks => {
      tracks.sort((a, b) => {
        return Math.sign(a.getEndedMs() - b.getEndedMs());
      });

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
    });
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

  ngOnDestroy() {
    this.trackSub.unsubscribe();
  }
}
