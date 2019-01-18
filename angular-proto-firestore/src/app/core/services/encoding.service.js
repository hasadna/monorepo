"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
exports.__esModule = true;
var core_1 = require("@angular/core");
var EncodingService = /** @class */ (function () {
    function EncodingService() {
    }
    EncodingService.prototype.encodeUint8ArrayToBase64String = function (binary) {
        return btoa(String.fromCharCode.apply(null, binary));
    };
    EncodingService.prototype.decodeBase64StringToUint8Array = function (base64) {
        var raw = window.atob(base64);
        var uint8Array = new Uint8Array(new ArrayBuffer(raw.length));
        for (var i = 0; i < raw.length; i++) {
            uint8Array[i] = raw.charCodeAt(i);
        }
        return uint8Array;
    };
    EncodingService = __decorate([
        core_1.Injectable()
    ], EncodingService);
    return EncodingService;
}());
exports.EncodingService = EncodingService;
