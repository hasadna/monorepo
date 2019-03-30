import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { FeedComponent, SingleScreenshotComponent, UserInfoComponent } from '@/components';

const appRoutes: Routes = [
  { path: 'home', component: FeedComponent },
  {
    path: 'single-screenshot/:storyId/:screenshot/:storyAuthor',
    component: SingleScreenshotComponent,
  },
  { path: 'info', component: UserInfoComponent },
  { path: '**', redirectTo: '/home' },
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
