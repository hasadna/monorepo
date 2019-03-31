import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { ProfilePagesComponent } from './profile-pages.component';
import {
  ContactsComponent,
  ProjectDetailsComponent,
  ProjectsComponent,
  UserDetailsComponent,
} from './profile-pages-components';

const routes: Routes = [
  { path: '', component: ProfilePagesComponent, redirectTo: 'projects' },
  { path: 'contacts', component: ContactsComponent },
  { path: 'user-details/:userId', component: UserDetailsComponent },
  { path: 'projects', component: ProjectsComponent },
  { path: 'project-details/:projectId', component: ProjectDetailsComponent },
  { path: '**', component: ProfilePagesComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ProfilePagesRoutingModule { }
