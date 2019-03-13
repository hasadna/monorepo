import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import { CoreModule } from './core/core.module';
import { FirebaseModule } from './import/firebase/firebase.module';
import { HeaderComponent } from './components/header/header.component';
import { UserInfoComponent } from './components/user-info/user-info.component';
import { FeedComponent } from './components/feed/feed.component';
import { AppRoutingModule } from './app-routing.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatToolbarModule, MatIconModule, MatButtonModule } from '@angular/material';
import { SingleScreenshotComponent } from './components/single-screenshot/single-screenshot.component';


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
    BrowserAnimationsModule,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

