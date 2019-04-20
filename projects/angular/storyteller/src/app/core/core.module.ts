import { NgModule } from '@angular/core';

import {
  FirebaseService,
  AuthService,
  AuthGuard,
  StoryService,
  ScreenService,
  NotificationService,
} from './services';

@NgModule({
  providers: [
    FirebaseService,
    AuthService,
    AuthGuard,
    StoryService,
    ScreenService,
    NotificationService,
  ],
})
export class CoreModule { }
