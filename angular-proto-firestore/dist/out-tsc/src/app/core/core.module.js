import * as tslib_1 from "tslib";
import { NgModule } from '@angular/core';
import { EncodingService, FirebaseService } from './services';
var CoreModule = /** @class */ (function () {
    function CoreModule() {
    }
    CoreModule = tslib_1.__decorate([
        NgModule({
            providers: [
                EncodingService,
                FirebaseService,
            ],
        })
    ], CoreModule);
    return CoreModule;
}());
export { CoreModule };
//# sourceMappingURL=core.module.js.map