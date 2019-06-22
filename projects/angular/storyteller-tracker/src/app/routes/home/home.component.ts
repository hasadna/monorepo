import { Component } from '@angular/core';
import { randstr64, randstr } from 'rndmjs';
import { Observable } from 'rxjs';

import { Story, StoryItem, Screenshot, StoryList } from '@/core/proto';
import {
  LoadingService,
  NotificationService,
  AuthService,
  FirebaseService,
} from '@/core/services';

interface StoryItemUI {
  oneliner: string;
  note: string;
  screenshot: string;
  isSaved: boolean;
}

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  storyItemList: StoryItemUI[] = [];
  storyToSave: Story;
  id: string;
  time: number;
  startMs: number;
  endMs: number;
  isStarted: boolean;
  isOpened: boolean;
  timer: number;
  display: number;
  isLoading: boolean;

  constructor(
    private isLoadingService: LoadingService,
    private notificationService: NotificationService,
    private authService: AuthService,
    private firebaseService: FirebaseService,
  ) {
    this.isLoadingService.isLoading = false;
    this.init(null);
  }

  init(id: string): void {
    this.id = id;
    this.time = 0;
    this.startMs = 0;
    this.endMs = 0;
    this.isStarted = false;
    this.isOpened = false;
    this.timer = null;
    this.display = 0;
    this.isLoading = false;
  }

  // Opens project
  open(): void {
    if (this.id) {
      this.init(this.id);
      this.isOpened = true;
    } else {
      this.notificationService.error('Please enter project id');
    }
  }

  // Closes project
  close(): void {
    this.stop();
    this.isOpened = false;
    this.storyItemList = [];
  }

  // Starts timer
  start(): void {
    this.storyToSave = new Story();
    this.startMs = Date.now();
    this.timer = window.setInterval(() => {
      this.endMs = Date.now();
      const ms: number = this.endMs - this.startMs;
      this.display = this.time + ms;
    }, 100);
    this.isStarted = true;
  }

  // Stops timer and sends story to firebase
  stopClock(): void {
    this.stop();
    this.isLoading = true;
    this.sendStory().subscribe(() => {
      this.isLoading = false;
    });
  }

  // Stops timers
  stop(): void {
    this.time += this.endMs - this.startMs;
    window.clearInterval(this.timer);
    this.isStarted = false;
  }

  sendStory(): Observable<void> {
    return new Observable(observer => {
      this.storyToSave.setStartTimeMs(this.startMs);
      this.storyToSave.setEndTimeMs(this.endMs);
      this.storyToSave.setProject(this.id);
      this.storyToSave.setId(randstr64(12));
      this.storyToSave.setAuthor(this.authService.email);
      const storyList = new StoryList();
      storyList.addStory(this.storyToSave);
      this.firebaseService
        .addStoryList(storyList, this.authService.email, this.endMs)
        .subscribe(() => {
          observer.next();
        });
    });
  }

  // 1561209830000 -> 1:23:50
  timeConversion(ms: number): string {
    function getZero(num: number): string {
      num = Math.floor(num);
      return ('0' + num).slice(-2);
    }

    const date: Date = new Date(ms);
    return Math.floor(date.getUTCHours()) + ':' +
      getZero(date.getMinutes()) + ':' +
      getZero(date.getSeconds());
  }

  // Adds story item on template
  addStoryItem(): void {
    this.storyItemList.unshift({
      oneliner: '',
      note: '',
      screenshot: null,
      isSaved: false,
    });
  }

  saveStoryItem(storyItemUI: StoryItemUI): void {
    this.isLoading = true;
    const storyItem = new StoryItem();
    // Add storyItem to current Story and sent it to firebase.
    // If screenshot exists also upload it to storage
    if (storyItemUI.screenshot) {
      const mime: string = this.base64MimeType(storyItemUI.screenshot);
      const filename: string = randstr(16) + '.' + this.mimeToExtension(mime);

      const screeshot = new Screenshot();
      screeshot.setFilename('storyteller/' + filename);
      screeshot.setScreenshot(storyItemUI.screenshot);

      this.firebaseService.uploadScreenshot(screeshot, mime).subscribe(url => {
        storyItemUI.screenshot = url;
        storyItem.setScreenshotFilename(filename);
        this.sendStoryItem(storyItemUI, storyItem);
      });
    } else {
      this.sendStoryItem(storyItemUI, storyItem);
    }
  }

  private sendStoryItem(storyItemUI: StoryItemUI, storyItem: StoryItem): void {
    storyItem.setOneliner(storyItemUI.oneliner);
    storyItem.setNote(storyItemUI.note);
    storyItem.setId(randstr64(10));
    storyItem.setTimeMs(Date.now());
    this.storyToSave.addItem(storyItem);
    this.stop();
    this.sendStory().subscribe(() => {
      this.start();
      this.isLoading = false;
      storyItemUI.isSaved = true;
    });
  }

  // Adds screenshot, when user press Ctrl+V
  onPaste(event: ClipboardEvent, storyItemUI: StoryItemUI) {
    if (storyItemUI.isSaved) {
      return;
    }
    const item: DataTransferItem = event.clipboardData.items[0];
    if (item.kind === 'file') {
      const blob: File = item.getAsFile();
      const reader = new FileReader();
      reader.onload = () => {
        storyItemUI.screenshot = reader.result.toString();
      };
      reader.readAsDataURL(blob);
    }
  }

  base64MimeType(base64: string): string {
    // 'data:image/png;base64,iVBORw0KGgoAAAAN...' -> 'image/png'
    const mime: RegExpMatchArray = base64.match(/data:([a-zA-Z0-9]+\/[a-zA-Z0-9-.+]+).*,.*/);
    if (mime && mime[1]) {
      return mime[1];
    } else {
      throw new Error('Mime not found');
    }
  }

  mimeToExtension(mime: string): string {
    return mime.split('/')[1];
  }
}
