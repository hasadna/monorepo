import { Component } from '@angular/core';
import { Router, NavigationStart } from '@angular/router';

import { HeaderService, AuthService, LoadingService, UserService } from '@/core/services';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent {
  url: string;

  constructor(
    public headerService: HeaderService,
    public authService: AuthService,
    public userService: UserService,
    public loadingService: LoadingService,
    private router: Router,
  ) {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        // Convert '/my-page?param=data' -> '/my-page'
        const parser: HTMLAnchorElement = document.createElement('a');
        parser.href = event.url;
        this.url = parser.pathname;
      }
    });
  }

  isVisible(): boolean {
    return this.authService.isInit &&
      this.authService.isOnline &&
      !this.loadingService.isLoading;
  }
}
