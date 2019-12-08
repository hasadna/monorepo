import { Component } from '@angular/core';

import { User } from '@/core/proto';
import { HeaderService, AuthService, UserService } from '@/core/services';

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
    public userService: UserService,
  ) {
    this.user = userService.user;
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
