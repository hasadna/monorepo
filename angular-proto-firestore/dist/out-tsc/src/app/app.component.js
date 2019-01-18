import * as tslib_1 from "tslib";
import { Component } from '@angular/core';
import { EncodingService, FirebaseService } from '@/core/services';
var AppComponent = /** @class */ (function () {
    function AppComponent(encodingService, firebaseService) {
        var _this = this;
        this.encodingService = encodingService;
        this.firebaseService = firebaseService;
        this.firebaseService.getStory().subscribe(function (story) {
            _this.story = story;
        });
    }
    AppComponent = tslib_1.__decorate([
        Component({
            selector: 'app-root',
            templateUrl: './app.component.html',
            styleUrls: ['./app.component.scss']
        }),
        tslib_1.__metadata("design:paramtypes", [EncodingService,
            FirebaseService])
    ], AppComponent);
    return AppComponent;
}());
export { AppComponent };
//# sourceMappingURL=app.component.js.map