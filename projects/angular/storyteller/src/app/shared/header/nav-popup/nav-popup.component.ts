import { Component } from '@angular/core';

import { HeaderService } from '@/core/services';

@Component({
  selector: 'nav-popup',
  templateUrl: './nav-popup.component.html',
  styleUrls: ['./nav-popup.component.scss'],
})
export class NavPopupComponent {
  constructor(public headerService: HeaderService) { }
}
