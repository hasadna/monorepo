import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { LibraryModule } from 'common/module';
import { AppComponent } from './app.component';
import { CoreModule } from './core/core.module';
import { FirebaseModule } from './import/firebase/firebase.module';

@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    BrowserModule,
    CoreModule,
    FirebaseModule,
    LibraryModule,
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule { }
