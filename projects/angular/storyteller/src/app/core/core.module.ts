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
  ],
})
export class CoreModule { }
