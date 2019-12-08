import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable()
export class ScreenService {
  width: number;
  height: number;
  resizeChanges = new Subject<void>();

  constructor() {
    this.resize();
    window.addEventListener('resize', () => {
      this.resize();
      this.resizeChanges.next();
    });
  }

  resize() {
    if (window.innerWidth) {
      this.width = window.innerWidth;
      this.height = window.innerHeight;
    } else if (document.documentElement && document.documentElement.clientWidth) {
      this.width = document.documentElement.clientWidth;
      this.height = document.documentElement.clientHeight;
    } else {
      this.width = document.body.clientWidth;
      this.height = document.body.clientHeight;
    }
  }
}
