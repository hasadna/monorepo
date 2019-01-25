import * as tslib_1 from "tslib";
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FeedComponent } from '@/components/feed/feed.component';
import { UserInfoComponent } from '@/components/user-info/user-info.component';
var appRoutes = [
    { path: 'home', component: FeedComponent },
    { path: 'info', component: UserInfoComponent },
    { path: '**', redirectTo: '/home' },
];
var AppRoutingModule = /** @class */ (function () {
    function AppRoutingModule() {
    }
    AppRoutingModule = tslib_1.__decorate([
        NgModule({
            declarations: [],
            imports: [
                CommonModule,
                RouterModule.forRoot(appRoutes),
            ],
            exports: [
                RouterModule,
            ]
        })
    ], AppRoutingModule);
    return AppRoutingModule;
}());
export { AppRoutingModule };
//# sourceMappingURL=app-routing.module.js.map