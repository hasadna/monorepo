import { NgModule } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';

import {
  FirebaseService,
  AuthService,
  AuthGuard,
  StoryService,
  ScreenService,
  NotificationService,
  LoadingService,
  ScrollService,
  HeaderService,
  FirestoryService,
  TrackerService,
  UserService,
} from './services';

@NgModule({
  providers: [
    FirebaseService,
    AuthService,
    AuthGuard,
    StoryService,
    ScreenService,
    NotificationService,
    LoadingService,
    ScrollService,
    HeaderService,
    FirestoryService,
    TrackerService,
    UserService,
    CookieService,
  ],
})
export class CoreModule { }
