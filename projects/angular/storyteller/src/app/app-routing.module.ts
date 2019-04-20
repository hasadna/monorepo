import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import {
  FeedComponent,
  UserInfoComponent,
  LoginComponent,
  SingleItemComponent,
} from '@/routes';
import { AuthGuard } from '@/core/services';

const appRoutes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  {
    path: '',
    canActivate: [AuthGuard],
    children: [
      { path: 'home', component: FeedComponent },
      { path: 'info/:email', component: UserInfoComponent },
      { path: 'single-item/:email/:storyId/:itemId', component: SingleItemComponent },
    ],
  },
  { path: '**', redirectTo: 'login' },
];

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    RouterModule.forRoot(appRoutes),
  ],
  exports: [
    RouterModule,
  ],
})
export class AppRoutingModule { }
