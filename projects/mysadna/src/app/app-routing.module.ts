import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { ContactsComponent, UserDetailsComponent, ProjectsComponent, ProjectDetailsComponent } from '@/components';

const routes: Routes = [
  { path: 'contacts', component: ContactsComponent },
  { path: 'user-details/:userId', component: UserDetailsComponent },
  { path: 'projects', component: ProjectsComponent },
  { path: 'project-details/:projectId', component: ProjectDetailsComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
