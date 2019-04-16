import { Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material';

import { ScreenshotDialogComponent } from '@/shared/screenshot-dialog';

export interface EasyStory {
  storyId: string;
  itemId: string;
  username: string;
  email: string;
  project: string;
  timestamp: number;
  oneliner: string;
  note: string;
  screenshot: string;
}

@Component({
  selector: 'app-story',
  templateUrl: './story.component.html',
  styleUrls: ['./story.component.scss'],
})
export class StoryComponent {
  @Input() easyStory: EasyStory;

  constructor(private matDialog: MatDialog) { }

  openScreenshot(): void {
    this.matDialog.open(ScreenshotDialogComponent, {
      data: this.easyStory.screenshot,
    });
  }
}
