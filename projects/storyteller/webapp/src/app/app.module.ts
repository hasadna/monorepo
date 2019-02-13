import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AngularFireModule } from '@angular/fire';
import { environment } from '../environments/environment';
import { AppComponent } from './app.component';
import { CoreModule } from './core/core.module';
import { FirebaseModule } from './import/firebase/firebase.module';
import { HeaderComponent } from './components/header/header.component';
import { UserInfoComponent } from './components/user-info/user-info.component';
import { FeedComponent } from './components/feed/feed.component';
import { AppRoutingModule } from './app-routing/app-routing.module';
import { SingleScreenshotComponent } from './components/single-screenshot/single-screenshot.component';
import { NgxImgZoomModule } from 'ngx-img-zoom';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    UserInfoComponent,
    FeedComponent,
    SingleScreenshotComponent,
  ],
  imports: [
    BrowserModule,
    CoreModule,
    FirebaseModule,
    AppRoutingModule,
    AngularFireModule.initializeApp(environment.firebase),
    NgxImgZoomModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
