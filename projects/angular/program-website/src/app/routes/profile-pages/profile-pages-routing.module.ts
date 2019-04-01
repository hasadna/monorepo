import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { ProfilePagesComponent } from './profile-pages.component';
import {
  ContactsComponent,
  ProjectDetailsComponent,
  ProjectsComponent,
  UserDetailsComponent,
} from './components';

const routes: Routes = [
  {
    path: '', component: ProfilePagesComponent, children: [
      { path: '', component: ProjectsComponent },
      { path: 'contacts', component: ContactsComponent },
      { path: 'user-details/:userId', component: UserDetailsComponent },
      { path: 'projects', component: ProjectsComponent },
      { path: 'project-details/:projectId', component: ProjectDetailsComponent },
      { path: '**', component: ProjectsComponent },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ProfilePagesRoutingModule { }
