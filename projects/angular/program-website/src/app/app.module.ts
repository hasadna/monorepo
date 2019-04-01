import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { CoreModule } from './core/core.module';
import { FirebaseModule } from './import/firebase/firebase.module';
import { AppRoutingModule } from './app-routing.module';
import { HomeComponent } from '@/routes';
import { CommonModule } from 'common/module';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
  ],
  imports: [
    BrowserModule,
    CoreModule,
    FirebaseModule,
    AppRoutingModule,
    CommonModule,
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule { }
