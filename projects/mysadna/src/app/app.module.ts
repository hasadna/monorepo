import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ComponentList } from './components';
import { FirebaseModule } from './import/firebase/firebase.module';
import { ServiceList } from './services';
import { SharedList } from './shared';

@NgModule({
  declarations: [
    AppComponent,
    ...ComponentList,
    ...SharedList
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
