import { Injectable } from '@angular/core';

interface Link {
  label: string;
  url: string;
}

@Injectable()
export class HeaderService {
  isPushed: boolean = false;
  routes: Link[] = [
    { label: 'Feed', url: '/feed' },
    { label: 'Time Tracking', url: '/tracking' },
  ];

  toggle(): void {
    this.isPushed = !this.isPushed;
  }

  close(): void {
    this.isPushed = false;
  }
}
