import { NgModule } from '@angular/core';
import {
  MatButtonModule,
  MatIconModule,
  MatToolbarModule,
  MatProgressSpinnerModule,
  MatDialogModule,
  MatSnackBarModule,
  MatSelectModule,
  MatAutocompleteModule,
} from '@angular/material';

const MaterialImports = [
  MatButtonModule,
  MatIconModule,
  MatToolbarModule,
  MatProgressSpinnerModule,
  MatDialogModule,
  MatSnackBarModule,
  MatSelectModule,
  MatAutocompleteModule,
];

@NgModule({
  imports: MaterialImports,
  declarations: [],
  providers: [],
  exports: MaterialImports,
})
export class MaterialModule { }
