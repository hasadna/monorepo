import { NgModule } from '@angular/core';

import {
  FirebaseService,
  AuthService,
  AuthGuard,
  StoryService,
  ScreenService,
} from './services';

@NgModule({
  providers: [
    FirebaseService,
    AuthService,
    AuthGuard,
    StoryService,
    ScreenService,
  ],
})
export class CoreModule { }
