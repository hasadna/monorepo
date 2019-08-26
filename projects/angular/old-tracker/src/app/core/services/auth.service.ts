import { Injectable } from '@angular/core';
import { AngularFireAuth } from '@angular/fire/auth';
import { Router } from '@angular/router';
import { Observable, Subscriber } from 'rxjs';
import * as firebase from 'firebase/app';

@Injectable()
export class AuthService {
  isOnline: boolean;
  isInit: boolean = false;
  email: string;

  constructor(
    private router: Router,
    private angularFireAuth: AngularFireAuth,
  ) {
    this.angularFireAuth.authState.subscribe(userData => {
      this.isInit = true;
      this.isOnline = !!userData && !!userData.email;
      if (this.isOnline) {
        this.email = userData.email;
      } else {
        this.router.navigate(['/login']);
      }
    });
  }

  logInWithGoogle(): Observable<void> {
    return new Observable((observer: Subscriber<void>) => {
      this.angularFireAuth.auth.signInWithPopup(
        new firebase.auth.GoogleAuthProvider(),
      )
        .then(() => observer.next())
        .catch(() => observer.error());
    });
  }

  logOut(): Observable<void> {
    return new Observable((observer: Subscriber<void>) => {
      this.angularFireAuth.auth.signOut()
        .then(() => observer.next())
        .catch(() => observer.error());
    });
  }
}
