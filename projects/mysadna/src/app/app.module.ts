import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ComponentList } from './components';
import { FirebaseModule } from './import/firebase/firebase.module';
import { ServiceList } from './services';

@NgModule({
  declarations: [
    AppComponent,
    ...ComponentList
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FirebaseModule
  ],
  providers: [...ServiceList],
  bootstrap: [AppComponent]
})
export class AppModule { }
