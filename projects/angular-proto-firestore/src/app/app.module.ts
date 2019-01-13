import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

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
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
