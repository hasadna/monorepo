import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { CoreModule } from './core/core.module';
import { FirebaseModule } from './import/firebase/firebase.module';
import { AboutComponent } from './about-component/about.component';
import { ProjectsComponent } from './projects-component/projects.component';
import { ParticipantsComponent } from './participants-component/participants.component';
import { ContactUsComponent } from './contact-us-component/contact-us.component';
import {RouterModule} from '@angular/router';
import { AppRoutingModule } from './app-routing.module';

@NgModule({
  declarations: [
    AppComponent,
    AboutComponent,
    ProjectsComponent,
    ParticipantsComponent,
    ContactUsComponent,
  ],
  imports: [
    BrowserModule,
    CoreModule,
    FirebaseModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
