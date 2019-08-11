import { Injectable } from '@angular/core';

@Injectable()
export class TimeService {
  // 1561209830000 -> 1:23:50
  timestampToTime(ms: number): string {
    function getZero(num: number): string {
      num = Math.floor(num);
      return ('0' + num).slice(-2);
    }

    const date: Date = new Date(ms);
    return Math.floor(date.getUTCHours()) + ':' +
      getZero(date.getMinutes()) + ':' +
      getZero(date.getSeconds());
  }
}
