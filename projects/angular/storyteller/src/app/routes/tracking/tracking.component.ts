import { Component } from '@angular/core';

import {
  LoadingService,
} from '@/core/services';

@Component({
  selector: 'page-tracking',
  templateUrl: './tracking.component.html',
  styleUrls: ['./tracking.component.scss'],
})
export class TrackingComponent {
  constructor(public loadingService: LoadingService) {
    this.loadingService.isLoading = false;
  }
}
