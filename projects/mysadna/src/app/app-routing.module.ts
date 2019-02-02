import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { UsersComponent } from './components/users/users.component';
import { UserDetailsComponent } from './components/user-details/user-details.component';
import { ProjectsComponent } from './components/projects/projects.component';
import { ProjectDetailsComponent } from './components/project-details/project-details.component';

const routes: Routes = [
  { path: 'users', component: UsersComponent },
  { path: 'user-details/:userId', component: UserDetailsComponent },
  { path: 'projects', component: ProjectsComponent },
  { path: 'project-details/:projectId', component: ProjectDetailsComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
