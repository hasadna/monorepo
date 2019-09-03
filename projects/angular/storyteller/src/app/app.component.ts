import { Component } from '@angular/core';

import { AuthService, TreackerService, UserService } from '@/core/services';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
})
export class AppComponent {
  constructor(
    public authService: AuthService,
    public treackerService: TreackerService,
    public userService: UserService,
  ) { }
}
