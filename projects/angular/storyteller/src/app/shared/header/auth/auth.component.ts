import { Component } from '@angular/core';

import { User } from '@/core/proto';
import { HeaderService, AuthService } from '@/core/services';

@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.scss'],
})
export class AuthComponent {
  isHidden: boolean = true;
  user: User;

  constructor(
    private headerService: HeaderService,
    public authService: AuthService,
  ) {
    this.user = authService.user;
  }

  toggle(): void {
    this.headerService.close();
    this.isHidden = !this.isHidden;
  }

  close(): void {
    this.isHidden = true;
  }

  logOut(): void {
    this.authService.logOut().subscribe();
  }
}
