import { Component, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';

import { AuthService, LoadingService } from '@/core/services';

@Component({
  selector: 'page-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnDestroy {
  authSub = new Subscription();

  constructor(
    private authService: AuthService,
    public loadingService: LoadingService,
    private router: Router,
  ) {
    this.authSub = this.authService.getOnline().subscribe(isOnline => {
      if (isOnline) {
        this.router.navigate(['/feed']);
      } else {
        this.loadingService.isLoading = false;
      }
    });
  }

  logInWithGoogle(): void {
    this.loadingService.isLoading = true;
    this.authService.logInWithGoogle().subscribe(() => {
      this.router.navigate(['/feed']);
    }, () => {
      this.loadingService.isLoading = false;
    });
  }

  ngOnDestroy() {
    this.authSub.unsubscribe();
  }
}
