import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import {
  FeedComponent,
  UserInfoComponent,
  LoginComponent,
  MomentComponent,
  TrackingComponent,
} from '@/routes';
import { LibraryModule } from 'common/module';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CoreModule } from './core/core.module';
import { FirebaseModule } from './import';
import { SharedModule } from './shared';

@NgModule({
  declarations: [
    AppComponent,
    UserInfoComponent,
    FeedComponent,
    LoginComponent,
    MomentComponent,
    TrackingComponent,
  ],
  imports: [
    BrowserModule,
    CoreModule,
    FirebaseModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    LibraryModule,
    SharedModule,
  ],
  bootstrap: [AppComponent],
})
export class AppModule { }
