import { NgModule } from '@angular/core';

import {
  EncodingService,
  FirebaseService,
  StoryService
} from './services';

@NgModule({
  providers: [
    EncodingService,
    FirebaseService,
    StoryService
  ],
})
export class CoreModule { }
