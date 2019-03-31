import { NgModule } from '@angular/core';

import { ProfilePagesRoutingModule } from './profile-pages-routing.module';
import { ProfilePagesComponent } from './profile-pages.component';
import { ComponentList } from './profile-pages-components';
import { SharedList } from './profile-pages-shared';
import { CommonModule } from 'common/module';
import { FirebaseService } from '@/core/services';

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
  providers: [FirebaseService],
  bootstrap: [ProfilePagesComponent],
})
export class ProfilePagesModule { }
export function ProfilePagesFactory() {
  return ProfilePagesModule;
}
