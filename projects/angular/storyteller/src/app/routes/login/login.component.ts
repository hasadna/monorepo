import { Component } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from '@/core/services';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  constructor(
    private authService: AuthService,
    private router: Router,
  ) {
    this.authService.anonymousLogIn().subscribe(() => {
      this.router.navigate(['/home']);
    });
  }
}
