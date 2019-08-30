import { Component, OnDestroy } from '@angular/core';
import { FormControl } from '@angular/forms';
import { Subscription } from 'rxjs';

import { Story, Track } from '@/core/proto';
import {
  LoadingService,
  UserService,
  FirebaseService,
  Timer,
} from '@/core/services';

// If two tracks have pause between themselves less that the interval, they are shown as one.
const MAX_PAUSE_INTERVAL: number = 5 * 60 * 1000; // 5 min

@Component({
  selector: 'page-tracking',
  templateUrl: './tracking.component.html',
  styleUrls: ['./tracking.component.scss'],
})
export class TrackingComponent implements OnDestroy {
  selectedStory: Story;
  storySelect = new FormControl();
  tracks: string[] = [];
  storyDurationTime: string;
  trackSub = new Subscription();

  constructor(
    public loadingService: LoadingService,
    public userService: UserService,
    private firebaseService: FirebaseService,
  ) {
    this.loadingService.isLoading = false;
    this.storySelect.valueChanges.subscribe((story: Story) => {
      this.trackSub = this.firebaseService.getTracks(story.getId()).subscribe(tracks => {
        tracks.sort((a, b) => {
          return Math.sign(a.getEndedMs() - b.getEndedMs());
        });

        // Tracks are saved each minute automatically.
        // To not show 9000 short tracks, combine the several tracks to one.
        this.tracks = [];
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
        this.storyDurationTime = Timer.timestampToTime(storyDurationMs);
        this.tracks.reverse();
      });
    });
  }

  addTrack(startMs: number, endMs: number): void {
    let trackString: string = '';
    const startDate = new Date(startMs);
    trackString += startDate.getDate() + '.' + startDate.getMonth();
    trackString += ', ' + Timer.timestampToTime(startMs, false);
    trackString += ' - ' + Timer.timestampToTime(endMs, false);
    this.tracks.push(trackString);
  }

  ngOnDestroy() {
    this.trackSub.unsubscribe();
  }
}
