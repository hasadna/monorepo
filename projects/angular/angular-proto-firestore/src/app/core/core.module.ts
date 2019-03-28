import { NgModule } from '@angular/core';

import {
  FirebaseService,
} from './services';

@NgModule({
  providers: [
    FirebaseService,
  ],
})
export class CoreModule { }
