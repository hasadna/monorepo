import { Injectable } from '@angular/core';

@Injectable()
export class ClockService {
  // [0:00, 0:30, 1:00, ... , 23:30]
  generateClockList(): string[] {
    const clockList: string[] = [];
    for (let i = 0; i < 24; i++) {
      clockList.push(i + ':00');
    }
    for (let i = 0; i < 24; i++) {
      clockList.push(i + ':30');
    }
    this.sortClockList(clockList);
    return clockList;
  }

  sortClockList(clockList: string[]): void {
    // 0:00, 0:30, 1:00 etc
    clockList.sort((a, b) => {
      // '4:30' -> ['4', '30']
      const aTime: string[] = a.split(':');
      const bTime: string[] = b.split(':');

      // ['4', '30'] -> 4*60 + 30
      const aHours: number = parseInt(aTime[0]);
      const bHours: number = parseInt(bTime[0]);
      const aMinutes: number = 60 * aHours + parseInt(aTime[1]);
      const bMinutes: number = 60 * bHours + parseInt(bTime[1]);

      // Example: Math.sign(10 - 17) = -1
      return Math.sign(aMinutes - bMinutes);
    });
  }
}
