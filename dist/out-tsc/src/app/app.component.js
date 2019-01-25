import * as tslib_1 from "tslib";
import { Component } from '@angular/core';
import { EncodingService, FirebaseService } from '@/core/services';
var AppComponent = /** @class */ (function () {
    function AppComponent(firebaseService, encodingService) {
        var _this = this;
        this.firebaseService = firebaseService;
        this.encodingService = encodingService;
        if (firebaseService.isOnline) {
            this.load();
        }
        else {
            this.firebaseService.anonymousLogin().then(function () {
                _this.load();
            });
        }
    }
    AppComponent.prototype.load = function () {
        var _this = this;
        this.firebaseService.getstorylistAll().subscribe(function (storylist) {
            _this.storylist = storylist;
        });
    };
    AppComponent = tslib_1.__decorate([
        Component({
            selector: 'app-root',
            templateUrl: './app.component.html',
            styleUrls: ['./app.component.scss']
        }),
        tslib_1.__metadata("design:paramtypes", [FirebaseService,
            EncodingService])
    ], AppComponent);
    return AppComponent;
}());
export { AppComponent };
//# sourceMappingURL=app.component.js.map