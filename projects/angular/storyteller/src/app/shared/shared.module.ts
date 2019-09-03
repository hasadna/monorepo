import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { MaterialModule } from '@/import';
import { StoryComponent } from './story';
import {
  HeaderComponent,
  NavPopupComponent,
  AuthComponent,
  TrackerPopupComponent,
  TrackButtonComponent,
} from './header';
import { LoadingComponent } from './loading';
import { StoriesComponent } from './stories';
import { AvatarComponent } from './avatar';

const ExportDeclarations = [
  StoryComponent,
  HeaderComponent,
  LoadingComponent,
  StoriesComponent,
  AvatarComponent,
];
const ExportModules = [
  RouterModule,
  FormsModule,
  ReactiveFormsModule,
  HttpClientModule,
  CommonModule,
  MaterialModule,
];

@NgModule({
  imports: ExportModules,
  declarations: [
    ...ExportDeclarations,
    NavPopupComponent,
    AuthComponent,
    TrackerPopupComponent,
    TrackButtonComponent,
  ],
  exports: [
    ...ExportDeclarations,
    ...ExportModules,
  ],
})
export class SharedModule { }
