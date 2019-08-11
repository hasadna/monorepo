import { NgModule } from '@angular/core';

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
  ],
})
export class CoreModule { }
