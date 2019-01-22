import * as tslib_1 from "tslib";
import { Injectable } from '@angular/core';
import { AngularFirestore } from 'angularfire2/firestore';
import { map } from 'rxjs/operators';
import { StoryList } from '@/core/proto';
import { EncodingService } from './encoding.service';
import { AngularFireAuth } from 'angularfire2/auth';
var FirebaseService = /** @class */ (function () {
    function FirebaseService(db, encodingService, angularFireAuth) {
        var _this = this;
        this.db = db;
        this.encodingService = encodingService;
        this.angularFireAuth = angularFireAuth;
        this.angularFireAuth.authState.subscribe(function (userData) {
            _this.isOnline = !!userData;
        });
        this.protobin = this.db.collection('storyteller');
    }
    FirebaseService.prototype.getstorylistAll = function () {
        var _this = this;
        return this.protobin.snapshotChanges().pipe(map(function (action) { return action.map(function (a) {
            var firebaseElement = a.payload.doc.data();
            if (firebaseElement === undefined) {
                // Element not found
                return;
            }
            return _this.convertFirebaseElementToStory(firebaseElement);
        }); }));
    };
    FirebaseService.prototype.convertFirebaseElementToStory = function (firebaseElement) {
        //Convert firebaseElement to binary
        var binary = this.encodingService
            .decodeBase64StringToUint8Array(firebaseElement.proto);
        // Convert binary to book
        var storylist = StoryList.deserializeBinary(binary);
        return storylist;
    };
    FirebaseService.prototype.anonymousLogin = function () {
        return this.angularFireAuth.auth.signInAnonymously();
    };
    FirebaseService = tslib_1.__decorate([
        Injectable(),
        tslib_1.__metadata("design:paramtypes", [AngularFirestore,
            EncodingService,
            AngularFireAuth])
    ], FirebaseService);
    return FirebaseService;
}());
export { FirebaseService };
//# sourceMappingURL=firebase.service.js.map