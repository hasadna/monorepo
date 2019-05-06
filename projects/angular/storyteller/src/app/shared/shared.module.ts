import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { MaterialModule } from '@/import';
import { StoryComponent } from './story';
import { ProjectSelectComponent } from './project-select';
import { HeaderComponent } from './header';
import { ScreenshotDialogComponent } from './screenshot-dialog';
import { LoadingComponent } from './loading';

const ExportDeclarations = [
  StoryComponent,
  ProjectSelectComponent,
  HeaderComponent,
  ScreenshotDialogComponent,
  LoadingComponent,
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
  entryComponents: [ScreenshotDialogComponent],
})
export class SharedModule { }
