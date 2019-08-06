import { Injectable } from '@angular/core';

@Injectable()
export class LoadingService {
  isLoading: boolean = true;

  load(): void {
    this.isLoading = true;
  }

  stop(): void {
    this.isLoading = false;
  }
}
