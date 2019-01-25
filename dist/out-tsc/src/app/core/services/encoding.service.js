import * as tslib_1 from "tslib";
import { Injectable } from '@angular/core';
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
    EncodingService = tslib_1.__decorate([
        Injectable()
    ], EncodingService);
    return EncodingService;
}());
export { EncodingService };
//# sourceMappingURL=encoding.service.js.map