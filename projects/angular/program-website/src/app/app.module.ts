import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { LibraryModule } from 'common/module';
import { AppComponent } from './app.component';
import { CoreModule } from './core/core.module';
import { FirebaseModule } from './import/firebase/firebase.module';
import { AppRoutingModule } from './app-routing.module';
import { HomeComponent } from '@/routes';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    CoreModule,
    FirebaseModule,
    LibraryModule,
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule { }
