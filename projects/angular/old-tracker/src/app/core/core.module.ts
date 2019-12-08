import { NgModule } from '@angular/core';

import {
  FirebaseService,
  AuthService,
  AuthGuard,
  ScreenService,
  NotificationService,
  LoadingService,
  ScrollService,
  BinaryService,
  TimeService,
} from './services';

@NgModule({
  providers: [
    FirebaseService,
    AuthService,
    AuthGuard,
    ScreenService,
    NotificationService,
    LoadingService,
    ScrollService,
    BinaryService,
    TimeService,
  ],
})
export class CoreModule { }
