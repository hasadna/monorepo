import { Injectable } from '@angular/core';
import { AngularFireAuth } from '@angular/fire/auth';
import { Observable, Subject, of } from 'rxjs';
import * as firebase from 'firebase/app';

@Injectable()
export class AuthService {
  isOnline: boolean;
  isInit: boolean = false;
  email: string;
  onlineChanges = new Subject<boolean>();

  constructor(private angularFireAuth: AngularFireAuth) {
    this.angularFireAuth.authState.subscribe(userData => {
      this.isInit = true;
      this.isOnline = !!userData && !!userData.email;
      if (this.isOnline) {
        this.email = userData.email;
      }
      this.onlineChanges.next(this.isOnline);
    });
  }

  logInWithGoogle(): Observable<void> {
    return new Observable(observer => {
      this.angularFireAuth.auth.signInWithPopup(
        new firebase.auth.GoogleAuthProvider(),
      )
        .then(() => observer.next())
        .catch(() => observer.error());
    });
  }

  logOut(): Observable<void> {
    return new Observable(observer => {
      this.angularFireAuth.auth.signOut()
        .then(() => observer.next())
        .catch(() => observer.error());
    });
  }

  getOnline(): Observable<boolean> {
    if (this.isInit) {
      return of(this.isOnline);
    } else {
      return this.onlineChanges;
    }
  }
}
