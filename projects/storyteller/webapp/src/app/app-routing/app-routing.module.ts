import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { FeedComponent } from '@/components/feed/feed.component';
import { UserInfoComponent } from '@/components/user-info/user-info.component';

const appRoutes: Routes = [
  {path: 'home', component: FeedComponent, children:[
    {path:':project', component:FeedComponent}
  ]},
  {path: 'info', component: UserInfoComponent},
   {path: '**', redirectTo:'/home'},
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
