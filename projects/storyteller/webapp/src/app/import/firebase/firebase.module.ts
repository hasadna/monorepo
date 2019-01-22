import { NgModule } from '@angular/core';
import { AngularFireModule } from 'angularfire2';
import { AngularFireAuth, AngularFireAuthModule } from 'angularfire2/auth';
import { AngularFireDatabase } from 'angularfire2/database';
import { AngularFirestoreModule } from 'angularfire2/firestore';

import { config } from './config';
import { FirestoreSettingsToken } from '@angular/fire/firestore';
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
  ],
  exports: [
    AngularFirestoreModule,
    AngularFireAuthModule,
    
  ],
})
export class FirebaseModule { }
