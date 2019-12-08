import { Component } from '@angular/core';

import { AuthService, TrackerService, UserService } from '@/core/services';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
})
export class AppComponent {
  constructor(
    public authService: AuthService,
    public trackerService: TrackerService,
    public userService: UserService,
  ) { }
}
