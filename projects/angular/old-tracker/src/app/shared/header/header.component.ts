import { Component } from '@angular/core';

import { AuthService } from '@/core/services';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent {
  constructor(public authService: AuthService) { }

  logOut(): void {
    this.authService.logOut().subscribe();
  }
}
