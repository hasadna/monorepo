import { Component, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormControl } from '@angular/forms';
import { Observable, Subscription } from 'rxjs';
import { randstr } from 'rndmjs';

import { Story, Moment, Track } from '@/core/proto';
import { Screenshot } from '@/core/interfaces';
import {
  FirebaseService,
  LoadingService,
  BinaryService,
  TimeService,
  NotificationService,
  AuthService,
} from '@/core/services';

interface MomentUI {
  note: string;
  screenshot: string;
  timestamp: number;
  isSaved: boolean;
  isSaving: boolean;
  isScreenshotLoading: boolean;
}

class Tracker {
  isTracking: boolean = false;
  startMs: number = 0;
  endMs: number = 0;
  sendedMs: number = 0;
  trackedMs: number = 0;
  savedMs: number = 0;
  timer: number;
  autosend: number;
  time: string = '';

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

  displayMs(ms: number) {
    this.trackedMs = this.savedMs + ms;
  }
}

@Component({
  selector: 'app-story',
  templateUrl: './story.component.html',
  styleUrls: ['./story.component.scss'],
})
export class StoryComponent implements OnDestroy {
  isInit: boolean = false;
  story: Story;
  storySubscription = new Subscription();
  sharedSubscription = new Subscription();
  tracksSubscription = new Subscription();
  momentsSubscription = new Subscription();
  momentList: MomentUI[] = [];
  tracker = new Tracker();
  sharedToggler = new FormControl();
  isStoryUpdating: boolean = false;
  tracks: Track[] = [];
  isMomentsLoading: boolean = true;

  constructor(
    private activatedRoute: ActivatedRoute,
    private firebaseService: FirebaseService,
    public loadingService: LoadingService,
    private router: Router,
    private binaryService: BinaryService,
    public timeService: TimeService,
    private notificationService: NotificationService,
    private authService: AuthService,
  ) {
    this.loadStory();

    // When user clicks on "Shared" toggler
    this.sharedSubscription = this.sharedToggler.valueChanges.subscribe(isShared => {
      if (this.isStoryUpdating) {
        return;
      }
      if (this.authService.email !== this.story.getAuthor()) {
        this.notificationService.error('Only author can make a story shared');
        this.sharedToggler.setValue(this.story.getIsShared(), { emitEvent: false });
        return;
      }
      this.isStoryUpdating = true;
      this.story.setIsShared(isShared);

      // Save "Shared / Unshared"
      this.firebaseService.updateStory(this.story).subscribe(() => {
        const message: string = this.story.getIsShared() ? 'Shared' : 'Unshared';
        this.notificationService.success(message);
        this.sharedToggler.setValue(this.story.getIsShared(), { emitEvent: false });
        this.isStoryUpdating = false;
      });
    });
  }

  private loadStory(): void {
    const storyId: string = this.activatedRoute.snapshot.params['id'];
    this.storySubscription = this.firebaseService.getStory(storyId).subscribe(story => {
      if (story) {
        this.story = story;
        this.sharedToggler.setValue(this.story.getIsShared(), { emitEvent: false });
        if (!this.isInit) {
          // Load tracks and moments just once
          this.isInit = true;
          this.loadTracks(storyId);
          this.loadMoments(storyId);
        }
      } else {
        this.router.navigate(['/home']);
      }
    });
  }

  private loadTracks(storyId: string): void {
    this.tracksSubscription = this.firebaseService.getTracks().subscribe(tracks => {
      this.tracksSubscription.unsubscribe();
      this.tracks = tracks.filter(track => track.getStoryId() === storyId);
      // Display loaded tracks
      for (const track of this.tracks) {
        this.tracker.savedMs += track.getEndedMs() - track.getStartedMs();
      }
      this.tracker.displayMs(0);
      this.loadingService.stop();
    });
  }

  private loadMoments(storyId: string): void {
    this.momentsSubscription = this.firebaseService.getMoments().subscribe(moments => {
      this.momentsSubscription.unsubscribe();
      this.isMomentsLoading = false;
      moments = moments.filter(moment => moment.getStoryId() === storyId);
      moments.sort((a, b) => {
        // Newest firts
        return Math.sign(b.getTimestampMs() - a.getTimestampMs());
      });
      for (const moment of moments) {
        const momentUI: MomentUI = {
          note: moment.getNote(),
          screenshot: null,
          timestamp: moment.getTimestampMs(),
          isSaved: true,
          isSaving: false,
          isScreenshotLoading: true,
        };
        const index: number = this.momentList.push(momentUI) - 1;

        // We have only relative screenshot url. Getting full link takes some time.
        if (moment.getScreenshot()) {
          this.firebaseService.getScreenshotURL(moment.getScreenshot()).subscribe(url => {
            // Display loading of screenshot image
            const screenshot = new Image();
            screenshot.onload = () => {
              momentUI.isScreenshotLoading = false;
            };
            screenshot.src = url;
            momentUI.screenshot = url;
            this.momentList[index] = momentUI;
          }, () => {
            this.notificationService.error('Screenshot not found');
          });
        } else {
          this.momentList[index].isScreenshotLoading = false;
        }
      }
    });
  }

  // Starts tracker
  start(): void {
    this.tracker.startMs = Date.now();
    this.tracker.sendedMs = this.tracker.startMs;
    this.tracker.timer = window.setInterval(() => {
      this.tracker.endMs = Date.now();
      this.tracker.displayMs(this.tracker.endMs - this.tracker.startMs);
    }, 100);
    this.tracker.start();

    const autoSaveIntervalMin: number = 1; // Save track each minute
    this.tracker.autosend = window.setInterval(() => {
      if (this.tracker.isTracking) {
        this.sendTrack().subscribe();
      }
    }, autoSaveIntervalMin * 60 * 1000);
  }

  // Stops tracker
  stop(): void {
    this.sendTrack().subscribe(() => {
      this.notificationService.success('Track saved');
    });
    this.tracker.displayMs(this.tracker.endMs - this.tracker.startMs);
    this.tracker.savedMs += this.tracker.endMs - this.tracker.startMs;
    this.tracker.stop();
  }

  private sendTrack(): Observable<void> {
    return new Observable(observer => {
      const now: number = Date.now();
      const trackedInterval: number = now - this.tracker.sendedMs;
      if (trackedInterval > 1000) {
        // Save only the tracks, which are more than 1 sec
        const track = new Track();
        track.setStoryId(this.story.getId());
        track.setStartedMs(this.tracker.sendedMs);
        track.setEndedMs(now);
        this.firebaseService.addTrack(track).subscribe(() => {
          observer.next();
        });
        this.tracker.sendedMs = now;
      }
    });
  }

  // Adds moment on template
  addMoment(): void {
    this.momentList.unshift({
      note: '',
      screenshot: null,
      timestamp: null,
      isSaved: false,
      isSaving: false,
      isScreenshotLoading: false,
    });
  }

  // Sends screenshot to storage and Moment to db
  saveMoment(momentUI: MomentUI): void {
    if (momentUI.screenshot || momentUI.note) {
      momentUI.isSaving = true;
      const moment = new Moment();
      moment.setNote(momentUI.note);
      moment.setStoryId(this.story.getId());

      if (momentUI.screenshot) {
        const { imageHeader, base64 } = this.binaryService.parseImageBinary(momentUI.screenshot);
        const mime: string = this.binaryService.imageHeaderToMime(imageHeader);
        const filename: string = randstr(16) + '.' + this.binaryService.mimeToExtension(mime);

        const screeshot: Screenshot = {
          filename: 'storyteller/' + filename,
          base64: base64,
          mime: mime,
        };

        this.firebaseService.uploadScreenshot(screeshot).subscribe(url => {
          moment.setScreenshot(filename);
          this.sendMoment(moment, momentUI);
        });
      } else {
        this.sendMoment(moment, momentUI);
      }
    } else {
      this.notificationService.error('Empty Moment can not be saved');
    }
  }

  // sendMoment() is next step after saveMoment()
  private sendMoment(moment: Moment, momentUI: MomentUI): void {
    moment.setTimestampMs(Date.now());
    momentUI.timestamp = moment.getTimestampMs();
    this.firebaseService.addMoment(moment).subscribe(() => {
      momentUI.isSaved = true;
      momentUI.isSaving = false;
    });
  }

  // Adds screenshot, when user press Ctrl+V
  onPaste(event: ClipboardEvent, moment: MomentUI) {
    if (moment.isSaved) {
      return;
    }
    const item: DataTransferItem = event.clipboardData.items[0];
    if (item.kind === 'file') {
      const blob: File = item.getAsFile();
      const reader = new FileReader();
      reader.onload = () => {
        moment.screenshot = reader.result.toString();
      };
      reader.readAsDataURL(blob);
    }
  }

  ngOnDestroy() {
    this.storySubscription.unsubscribe();
    this.sharedSubscription.unsubscribe();
    this.tracksSubscription.unsubscribe();
    this.momentsSubscription.unsubscribe();
    this.tracker.stop();
  }
}
