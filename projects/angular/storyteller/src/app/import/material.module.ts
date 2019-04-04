import { NgModule } from '@angular/core';
import {
  MatButtonModule,
  MatIconModule,
  MatToolbarModule,
  MatProgressSpinnerModule,
  MatDialogModule,
} from '@angular/material';

const MaterialImports = [
  MatButtonModule,
  MatIconModule,
  MatToolbarModule,
  MatProgressSpinnerModule,
  MatDialogModule,
];

@NgModule({
  imports: MaterialImports,
  declarations: [],
  providers: [],
  exports: MaterialImports,
})
export class MaterialModule { }
