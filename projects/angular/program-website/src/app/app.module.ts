import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { CommonModule } from 'common/module';
import { AppComponent } from './app.component';
import { CoreModule } from './core/core.module';
import { FirebaseModule } from './import/firebase/firebase.module';
import { AppRoutingModule } from './app-routing.module';

@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    BrowserModule,
    CoreModule,
    FirebaseModule,
<<<<<<< HEAD:projects/program-website/src/app/app.module.ts
    AppRoutingModule,
=======
    CommonModule,
>>>>>>> a176109c6f6f9279b38ce7c9c3866a5b2b399406:projects/angular/program-website/src/app/app.module.ts
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule { }
