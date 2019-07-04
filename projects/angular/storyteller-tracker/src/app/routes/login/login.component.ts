import { Component } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService, LoadingService } from '@/core/services';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  constructor(
    public authService: AuthService,
    public loadingService: LoadingService,
    private router: Router,
  ) {
    this.loadingService.isLoading = false;
  }

  logInWithGoogle(): void {
    this.loadingService.isLoading = true;
    this.authService.logInWithGoogle().subscribe(() => {
      this.router.navigate(['/home']);
    });
  }

  logOut(): void {
    this.authService.logOut().subscribe();
  }
}
