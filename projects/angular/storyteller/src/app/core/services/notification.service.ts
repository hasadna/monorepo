import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material';

export enum NotifierStatus {
  success = 'st-snack-success',
  info = 'st-snack-info',
  warning = 'st-snack-warning',
  error = 'st-snack-error',
}

@Injectable()
export class NotificationService {
  private duration: number;

  constructor(private snackbar: MatSnackBar) {
    this.duration = 3000;
  }

  snack(
    message: string,
    action: string = '',
    duration: number = null,
    status: NotifierStatus,
  ): void {
    if (!message) {
      return;
    }

    this.snackbar.open(message, action, {
      duration: duration ? duration : this.duration,
      panelClass: [status],
    });
  }

  success(
    message: string,
    action: string = '',
    duration: number = null,
  ): void {
    this.snack(message, action, duration, NotifierStatus.success);
  }

  info(message: string, action: string = '', duration: number = null): void {
    this.snack(message, action, duration, NotifierStatus.info);
  }

  warning(
    message: string,
    action: string = '',
    duration: number = null,
  ): void {
    this.snack(message, action, duration, NotifierStatus.warning);
  }

  error(message: string, action: string = '', duration: number = null): void {
    this.snack(message, action, duration, NotifierStatus.error);
  }
}
