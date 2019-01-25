import * as tslib_1 from "tslib";
import { Component } from '@angular/core';
import { EncodingService, FirebaseService } from '@/core/services';
var FeedComponent = /** @class */ (function () {
    function FeedComponent(
    // private encodingService: EncodingService,
    firebaseService, encodingService) {
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
    FeedComponent.prototype.load = function () {
        var _this = this;
        this.firebaseService.getstorylistAll().subscribe(function (storylist) {
            _this.storylist = storylist;
        });
    };
    FeedComponent.prototype.ngOnInit = function () {
    };
    FeedComponent = tslib_1.__decorate([
        Component({
            selector: 'app-feed',
            templateUrl: './feed.component.html',
            styleUrls: ['./feed.component.scss']
        }),
        tslib_1.__metadata("design:paramtypes", [FirebaseService,
            EncodingService])
    ], FeedComponent);
    return FeedComponent;
}());
export { FeedComponent };
//# sourceMappingURL=feed.component.js.map