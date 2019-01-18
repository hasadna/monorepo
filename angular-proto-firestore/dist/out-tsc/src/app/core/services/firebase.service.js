import * as tslib_1 from "tslib";
import { Injectable } from '@angular/core';
import { AngularFirestore } from 'angularfire2/firestore';
import { map } from 'rxjs/operators';
import { StoryItem } from '@/core/proto';
import { EncodingService } from './encoding.service';
var FirebaseService = /** @class */ (function () {
    function FirebaseService(db, encodingService) {
        this.db = db;
        this.encodingService = encodingService;
        this.protobin = this.db.collection('storyteller');
    }
    FirebaseService.prototype.getStory = function () {
        var _this = this;
        return this.protobin
            .doc('Story')
            .snapshotChanges()
            .pipe(map(function (action) {
            var firebaseElement = action.payload.data();
            if (firebaseElement === undefined) {
                // Element not found
                return;
            }
            return _this.convertFirebaseElementToStory(firebaseElement);
        }));
    };
    FirebaseService.prototype.convertFirebaseElementToStory = function (firebaseElement) {
        // Convert firebaseElement to binary
        var binary = this.encodingService
            .decodeBase64StringToUint8Array(firebaseElement.proto);
        // Convert binary to book
        var story = StoryItem.deserializeBinary(binary);
        return story;
    };
    FirebaseService = tslib_1.__decorate([
        Injectable(),
        tslib_1.__metadata("design:paramtypes", [AngularFirestore,
            EncodingService])
    ], FirebaseService);
    return FirebaseService;
}());
export { FirebaseService };
//# sourceMappingURL=firebase.service.js.map