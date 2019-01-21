import { NgModule } from '@angular/core';
import { AngularFireModule } from 'angularfire2';
import { AngularFireAuth, AngularFireAuthModule } from 'angularfire2/auth';
import { AngularFireDatabase } from 'angularfire2/database';
import { AngularFirestoreModule, FirestoreSettingsToken  } from 'angularfire2/firestore';
//what i need to do with FirestoreSettingsToken
import { config } from './config';

//{ provide: FirestoreSettingsToken, useValue: {} }

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
