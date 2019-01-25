import * as tslib_1 from "tslib";
import { NgModule } from '@angular/core';
import { AngularFireModule } from 'angularfire2';
import { AngularFireAuth, AngularFireAuthModule } from 'angularfire2/auth';
import { AngularFireDatabase } from 'angularfire2/database';
import { AngularFirestoreModule, FirestoreSettingsToken } from 'angularfire2/firestore';
import { config } from './config';
var FirebaseModule = /** @class */ (function () {
    function FirebaseModule() {
    }
    FirebaseModule = tslib_1.__decorate([
        NgModule({
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
    ], FirebaseModule);
    return FirebaseModule;
}());
export { FirebaseModule };
//# sourceMappingURL=firebase.module.js.map