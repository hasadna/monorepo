import { Injectable } from '@angular/core';
import { AngularFireAuth } from '@angular/fire/auth';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';

@Injectable()
export class AuthService {
  isOnline: boolean;
  email: string;

  constructor(
    private router: Router,
    private angularFireAuth: AngularFireAuth,
  ) {
    this.angularFireAuth.authState.subscribe(userData => {
      this.isOnline = !!userData;
      if (this.isOnline) {
        this.email = userData.email;
      } else {
        this.router.navigate(['/login']);
      }
    });
  }

  anonymousLogIn(): Observable<void> {
    return new Observable(observer => {
      this.angularFireAuth.auth.signInAnonymously()
        .then(() => observer.next())
        .catch(() => observer.error());
    });
  }
}
