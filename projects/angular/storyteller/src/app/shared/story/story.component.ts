import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material';

import { ScreenshotDialogComponent } from '@/shared/screenshot-dialog';

export interface Snapshot {
  oneliner: string;
  note: string;
  screenshot: string;
}

export interface EasyStory {
  id: string;
  username: string;
  email: string;
  project: string;
  timestamp: number;
  snapshots: Snapshot[];
}

@Component({
  selector: 'app-story',
  templateUrl: './story.component.html',
  styleUrls: ['./story.component.scss'],
})
export class StoryComponent {
  @Input() easyStory: EasyStory;

  constructor(
    private router: Router,
    private matDialog: MatDialog,
  ) { }

  openStoryItem(): void {
    this.router.navigate(['/single-item', this.easyStory.email, this.easyStory.id]);
  }

  openScreenshot(screenshot: string): void {
    this.matDialog.open(ScreenshotDialogComponent, {
      data: screenshot,
    });
  }
}
