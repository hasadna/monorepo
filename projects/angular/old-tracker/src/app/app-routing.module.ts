import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import {
  HomeComponent,
  LoginComponent,
  StoryComponent,
} from '@/routes';
import { AuthGuard } from '@/core/services';

const appRoutes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  {
    path: '',
    canActivate: [AuthGuard],
    children: [
      { path: 'home', component: HomeComponent },
      { path: 'story/:id', component: StoryComponent },
    ],
  },
  { path: '**', redirectTo: 'login' },
];

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forRoot(appRoutes),
  ],
  exports: [
    RouterModule,
  ],
})
export class AppRoutingModule { }
