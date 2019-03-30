import { NgModule } from '@angular/core';
import { AngularFireModule } from '@angular/fire';
import { AngularFireAuth, AngularFireAuthModule } from '@angular/fire/auth';
import { AngularFireDatabase } from '@angular/fire/database';
import { AngularFirestoreModule, FirestoreSettingsToken } from '@angular/fire/firestore';

import { config } from './config';

@NgModule({
  imports: [
    AngularFireModule.initializeApp(config),
    AngularFirestoreModule,
    AngularFireAuthModule,
  ],
  declarations: [],
  providers: [
    AngularFireDatabase,
    AngularFireAuth,
    { provide: FirestoreSettingsToken, useValue: {} },
  ],
  exports: [
    AngularFirestoreModule,
    AngularFireAuthModule,
  ],
})
export class FirebaseModule { }
