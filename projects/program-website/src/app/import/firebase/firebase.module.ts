import { NgModule } from '@angular/core';
<<<<<<< HEAD
import { AngularFireModule } from 'angularfire2';
import { AngularFireAuth, AngularFireAuthModule } from 'angularfire2/auth';
import { AngularFireDatabase } from 'angularfire2/database';
import { AngularFirestoreModule } from 'angularfire2/firestore';
=======
import { AngularFireModule } from '@angular/fire';
import { AngularFireAuth, AngularFireAuthModule } from '@angular/fire/auth';
import { AngularFireDatabase } from '@angular/fire/database';
import { AngularFirestoreModule, FirestoreSettingsToken } from '@angular/fire/firestore';
>>>>>>> c5bb66de788bac5bc3f1e955a0c7eb234ea340d2

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
<<<<<<< HEAD
=======
    { provide: FirestoreSettingsToken, useValue: {} },
>>>>>>> c5bb66de788bac5bc3f1e955a0c7eb234ea340d2
  ],
  exports: [
    AngularFirestoreModule,
    AngularFireAuthModule,
  ],
})
export class FirebaseModule { }
