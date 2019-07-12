import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import {
  HomeComponent,
  UserInfoComponent,
  LoginComponent,
  MomentComponent,
} from '@/routes';
import { AuthGuard } from '@/core/services';

const appRoutes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  {
    path: '',
    canActivate: [AuthGuard],
    children: [
      { path: 'home', component: HomeComponent },
      { path: 'info/:email', component: UserInfoComponent },
      { path: 'moment/:email/:storyId/:momentId', component: MomentComponent },
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
