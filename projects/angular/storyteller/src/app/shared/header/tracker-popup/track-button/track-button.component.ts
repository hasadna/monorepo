import { Component } from '@angular/core';

import { TrackerService } from '@/core/services';

@Component({
  selector: 'track-button',
  templateUrl: './track-button.component.html',
  styleUrls: ['./track-button.component.scss'],
})
export class TrackButtonComponent {
  constructor(public trackerService: TrackerService) { }

  click(): void {
    if (this.trackerService.selectedStory) {
      if (!this.trackerService.isTracking) {
        this.trackerService.pressStart();
      } else {
        this.trackerService.pressStop();
      }
    }
  }
}
