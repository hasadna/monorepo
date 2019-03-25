import { NgModule } from '@angular/core';

import {
  EncodingService,
  FirebaseService
} from './services';

@NgModule({
  providers: [
    EncodingService,
    FirebaseService,
  ],
})
export class CoreModule { }
