import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import {
  ContactsComponent,
  ProjectDetailsComponent,
  ProjectsComponent,
  UserDetailsComponent
} from '@/components';

const routes: Routes = [
  { path: '', redirectTo: 'projects', pathMatch: 'full' },
  { path: 'contacts', component: ContactsComponent },
  { path: 'user-details/:userId', component: UserDetailsComponent },
  { path: 'projects', component: ProjectsComponent },
  { path: 'project-details/:projectId', component: ProjectDetailsComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
