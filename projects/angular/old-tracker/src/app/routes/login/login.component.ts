import { Component } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService, LoadingService } from '@/core/services';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
})
export class LoginComponent {
  constructor(
    public authService: AuthService,
    public loadingService: LoadingService,
    private router: Router,
  ) {
    this.loadingService.stop();
  }

  logInWithGoogle(): void {
    this.loadingService.load();
    this.authService.logInWithGoogle().subscribe(() => {
      this.router.navigate(['/home']);
    });
  }

  logOut(): void {
    this.authService.logOut().subscribe();
  }
}
