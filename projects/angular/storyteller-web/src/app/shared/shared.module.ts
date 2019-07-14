import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { MaterialModule } from '@/import';
import { StoryComponent } from './story';
import {
  ProjectSelectComponent,
  ProjectSelectCardComponent,
  ProjectSelectPopupComponent,
} from './project-select-components';
import { HeaderComponent } from './header';
import { LoadingComponent } from './loading';
import { FeedComponent } from './feed';
import { AvatarComponent } from './avatar';

const ExportDeclarations = [
  StoryComponent,
  ProjectSelectComponent,
  ProjectSelectCardComponent,
  ProjectSelectPopupComponent,
  HeaderComponent,
  LoadingComponent,
  FeedComponent,
  AvatarComponent,
];
const ExportModules = [
  FormsModule,
  ReactiveFormsModule,
  HttpClientModule,
  CommonModule,
  MaterialModule,
];

@NgModule({
  imports: [
    RouterModule,
    ...ExportModules,
  ],
  declarations: ExportDeclarations,
  providers: [],
  exports: [
    ...ExportDeclarations,
    ...ExportModules,
  ],
})
export class SharedModule { }
