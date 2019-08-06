import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material';

@Injectable()
export class NotificationService {
  constructor(private snackbar: MatSnackBar) { }

  private snack(message: string, status: string): void {
    if (message) {
      this.snackbar.open(message, null, {
        duration: 3000,
        panelClass: [status],
      });
    }
  }

  success(message: string): void {
    this.snack(message, 'notification-success');
  }

  error(message: string): void {
    this.snack(message, 'notification-error');
  }
}
