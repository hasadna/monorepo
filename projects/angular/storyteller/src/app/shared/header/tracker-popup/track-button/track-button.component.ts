import { Component } from '@angular/core';

import { TreackerService } from '@/core/services';

@Component({
  selector: 'track-button',
  templateUrl: './track-button.component.html',
  styleUrls: ['./track-button.component.scss'],
})
export class TrackButtonComponent {
  constructor(public treackerService: TreackerService) { }

  click(): void {
    if (this.treackerService.selectedStory) {
      if (!this.treackerService.isTracking) {
        this.treackerService.pressStart();
      } else {
        this.treackerService.pressStop();
      }
    }
  }
}
