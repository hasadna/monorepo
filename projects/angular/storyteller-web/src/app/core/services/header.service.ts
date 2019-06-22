import { Injectable } from '@angular/core';

@Injectable()
export class HeaderService {
  isPushed: boolean = false;
  selectedProjectId: string = 'all';

  toggle() {
    this.isPushed = !this.isPushed;
  }
}
