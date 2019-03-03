import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { FeedComponent } from '@/components/feed/feed.component';
import { UserInfoComponent } from '@/components/user-info/user-info.component';
import { SingleScreenshotComponent } from '@/components/single-screenshot/single-screenshot.component';

const appRoutes: Routes = [
  { path: 'home', component: FeedComponent },
  { path: 'single-screenshot/:sotryId/:screenshot', component: SingleScreenshotComponent },
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
  ]
})
export class AppRoutingModule { }
