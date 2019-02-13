import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {ProjectsComponent} from '@/projects-component/projects.component';
import {AboutComponent} from '@/about-component/about.component';
import {ContactUsComponent} from '@/contact-us-component/contact-us.component';
import {ParticipantsComponent} from '@/participants-component/participants.component';

const routes: Routes = [
  { path: 'projects', component: ProjectsComponent },
  { path: 'about', component: AboutComponent},
  { path: 'contact', component: ContactUsComponent},
  { path: 'participants', component: ParticipantsComponent}
];

@NgModule({
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})

export class AppRoutingModule { }
