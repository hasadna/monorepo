import { NgModule } from '@angular/core';
import { MatButtonModule, MatIconModule, MatToolbarModule } from '@angular/material';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import {
  FeedComponent,
  HeaderComponent,
  SingleScreenshotComponent,
  UserInfoComponent,
} from '@/components';
import { LibraryModule } from 'common/module';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CoreModule } from './core/core.module';
import { FirebaseModule } from './import/firebase/firebase.module';
import { StoryHeadlineComponent } from './shared/story-headline/story-headline.component';
import { StoryListContributorComponent } from './shared/story-list-contributor/story-list-contributor.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    UserInfoComponent,
    FeedComponent,
    SingleScreenshotComponent,
    StoryHeadlineComponent,
    StoryListContributorComponent,
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
    LibraryModule,
  ],
  bootstrap: [AppComponent],
})
export class AppModule { }
