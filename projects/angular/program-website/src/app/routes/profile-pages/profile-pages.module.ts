import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ProfilePagesRoutingModule } from './profile-pages-routing.module';
import { ProfilePagesComponent } from './profile-pages.component';
import { ComponentList } from './components';
import { SharedList } from './shared';

@NgModule({
  declarations: [
    ProfilePagesComponent,
   ...ComponentList,
   ...SharedList,
  ],
  imports: [
    ProfilePagesRoutingModule,
    CommonModule,
  ],
  providers: [],
  bootstrap: [ProfilePagesComponent],
})
export class ProfilePagesModule { }
export function ProfilePagesFactory() {
  return ProfilePagesModule;
}
