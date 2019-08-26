import { Injectable } from '@angular/core';
import { AngularFireAuth } from '@angular/fire/auth';
import { Router } from '@angular/router';
import { Observable, Subscription, Subject, of } from 'rxjs';
import * as firebase from 'firebase/app';

import { ReviewerConfig, User } from '@/core/proto';
import { FirebaseService } from './firebase.service';
import { StoryService } from './story.service';

@Injectable()
export class AuthService {
  isOnline: boolean;
  isInit: boolean = false;
  isUserLoading: boolean = true;
  email: string;
  user: User;
  reviewerConfig: ReviewerConfig;
  private onlineChanges = new Subject<boolean>();
  private reviewerConfigChanges = new Subject<ReviewerConfig>();
  private configSub = new Subscription();

  constructor(
    private router: Router,
    private angularFireAuth: AngularFireAuth,
    private firebaseService: FirebaseService,
    private storyService: StoryService,
  ) {
    this.angularFireAuth.authState.subscribe(userData => {
      this.isInit = true;
      this.isOnline = !!userData && !!userData.email;
      if (this.isOnline) {
        this.email = userData.email;
        this.connectReviewerConfig();
      } else {
        this.router.navigate(['/']);
        this.destroy();
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

  onloadReviewerConfig(): Observable<ReviewerConfig> {
    if (!this.isUserLoading) {
      return of(this.reviewerConfig);
    } else {
      return this.reviewerConfigChanges;
    }
  }

  private connectReviewerConfig(): void {
    this.isUserLoading = true;
    this.configSub = this.firebaseService.getReviewerConfig().subscribe(reviewerConfig => {
      this.reviewerConfig = reviewerConfig;
      this.user = this.storyService.getUser(this.email, reviewerConfig);
      this.isUserLoading = false;
      this.reviewerConfigChanges.next(reviewerConfig);
    });
  }

  destroy(): void {
    this.configSub.unsubscribe();
  }
}
