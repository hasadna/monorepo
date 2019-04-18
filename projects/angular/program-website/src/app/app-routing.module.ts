import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';

import { HomeComponent } from '@/routes';
import { ProfilePagesFactory } from '@/routes/profile-pages';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'profile-pages', loadChildren: ProfilePagesFactory },
  { path: '**', component: HomeComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {
    preloadingStrategy: PreloadAllModules,
  })],
  exports: [RouterModule],
})
export class AppRoutingModule { }
