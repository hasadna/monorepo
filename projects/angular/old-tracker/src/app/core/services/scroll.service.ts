import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { distinctUntilChanged } from 'rxjs/operators';

@Injectable()
export class ScrollService {
  scrollHeight: number;
  scroll: Observable<number>;
  private searchTerms = new Subject<number>();

  constructor() {
    document.onscroll = () => {
      this.searchTerms.next(this.getScrollTop());
    };

    this.scroll = this.searchTerms.pipe(
      distinctUntilChanged(),
    );
  }

  getScrollTop(): number {
    this.scrollHeight = document.documentElement.scrollHeight;
    return document.documentElement.scrollTop || document.body.scrollTop;
  }
}
