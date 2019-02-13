import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { CoreModule } from './core/core.module';
import { FirebaseModule } from './import/firebase/firebase.module';
<<<<<<< HEAD
import { AboutComponent } from './about-component/about.component';
import { ProjectsComponent } from './projects-component/projects.component';
import { ParticipantsComponent } from './participants-component/participants.component';
import { ContactUsComponent } from './contact-us-component/contact-us.component';
import {RouterModule} from '@angular/router';
import { AppRoutingModule } from './app-routing.module';
=======
>>>>>>> c5bb66de788bac5bc3f1e955a0c7eb234ea340d2

@NgModule({
  declarations: [
    AppComponent,
<<<<<<< HEAD
    AboutComponent,
    ProjectsComponent,
    ParticipantsComponent,
    ContactUsComponent,
=======
>>>>>>> c5bb66de788bac5bc3f1e955a0c7eb234ea340d2
  ],
  imports: [
    BrowserModule,
    CoreModule,
    FirebaseModule,
<<<<<<< HEAD
    AppRoutingModule
=======
>>>>>>> c5bb66de788bac5bc3f1e955a0c7eb234ea340d2
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
