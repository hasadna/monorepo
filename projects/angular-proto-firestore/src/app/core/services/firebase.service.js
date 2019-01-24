"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
exports.__esModule = true;
var core_1 = require("@angular/core");
var operators_1 = require("rxjs/operators");
var proto_1 = require("@/core/proto");
var FirebaseService = /** @class */ (function () {
    function FirebaseService(db, encodingService) {
        this.db = db;
        this.encodingService = encodingService;
        this.protobin = this.db.collection('protobin');
    }
    FirebaseService.prototype.getBook = function () {
        var _this = this;
        return this.protobin
            .doc('book')
            .snapshotChanges()
            .pipe(operators_1.map(function (action) {
            var firebaseElement = action.payload.data();
            if (firebaseElement === undefined) {
                // Element not found
                return;
            }
            return _this.convertFirebaseElementToBook(firebaseElement);
        }));
    };
    FirebaseService.prototype.convertFirebaseElementToBook = function (firebaseElement) {
        // Convert firebaseElement to binary
        var binary = this.encodingService
            .decodeBase64StringToUint8Array(firebaseElement.proto);
        // Convert binary to book
        var book = proto_1.Book.deserializeBinary(binary);
        return book;
    };
    FirebaseService = __decorate([
        core_1.Injectable()
    ], FirebaseService);
    return FirebaseService;
}());
exports.FirebaseService = FirebaseService;
