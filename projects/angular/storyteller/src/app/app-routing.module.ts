import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import {
  FeedComponent,
  UserInfoComponent,
  LoginComponent,
  MomentComponent,
  TrackingComponent,
} from '@/routes';
import { AuthGuard } from '@/core/services';

const appRoutes: Routes = [
  { path: '', component: LoginComponent },
  {
    path: '',
    canActivate: [AuthGuard],
    children: [
      { path: 'feed', component: FeedComponent },
      { path: 'info/:email', component: UserInfoComponent },
      { path: 'moment/:email/:storyId/:momentId', component: MomentComponent },
      { path: 'tracking', component: TrackingComponent },
    ],
  },
  { path: '**', component: LoginComponent },
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
