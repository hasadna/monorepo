import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { Subscription } from 'rxjs';

import { ScreenService } from '@/core/services';

// This component is temporarily used nowhere, because of lack of image zooming.
// Opening images directly by their links are used instead.
// TODO: remove it or fix issues.

@Component({
  selector: 'screenshot-dialog',
  templateUrl: './screenshot-dialog.component.html',
})
export class ScreenshotDialogComponent implements OnDestroy {
  isLoading: boolean = true;
  imageWidth: number;
  imageHeight: number;
  height: number;
  width: number;
  subscription = new Subscription();

  constructor(
    public dialogRef: MatDialogRef<ScreenshotDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public screenshot: string,
    private screenService: ScreenService,
  ) {
    this.getImageSize();
    this.subscription = this.screenService.resizeChanges.subscribe(() => {
      this.resize();
    });
  }

  resize(): void {
    this.resizeImage(
      this.imageWidth,
      this.imageHeight,
      this.screenService.width - 500,
      this.screenService.height - 100,
    );
  }

  resizeImage(pWidth: number, pHeight: number, sWidth: number, sHeight: number): void {
    const screenRatio: number = sWidth / sHeight;
    const picRatio: number = pWidth / pHeight;
    if (picRatio < screenRatio && pHeight > sHeight) {
      this.height = sHeight;
      this.width = pWidth * sHeight / pHeight;
    }
    if (picRatio >= screenRatio && pWidth > sWidth) {
      this.width = sWidth;
      this.height = pHeight * sWidth / pWidth;
    }
    this.isLoading = false;
  }

  private getImageSize(): void {
    const img = new Image();
    img.onload = () => {
      this.imageWidth = img.width;
      this.imageHeight = img.height;
      this.resize();
    };
    img.src = 'data:image/png;base64,' + this.screenshot;
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}
