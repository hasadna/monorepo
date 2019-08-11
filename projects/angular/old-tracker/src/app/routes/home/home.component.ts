import { Component, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';

import { Story } from '@/core/proto';
import { LoadingService, FirebaseService, NotificationService } from '@/core/services';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnDestroy {
  isStoriesLoading: boolean = true;
  storyList: Story[];
  subscription = new Subscription();

  constructor(
    public loadingService: LoadingService,
    private firebaseService: FirebaseService,
    private router: Router,
    private notificationService: NotificationService,
  ) {
    this.loadingService.stop();
    this.subscription = this.firebaseService.getStoryList().subscribe(storyList => {
      storyList.sort((a, b) => {
        // Newest firts
        return Math.sign(b.getStartedMs() - a.getStartedMs());
      });
      this.storyList = storyList.slice(0, 10); // First 10 stories
      this.isStoriesLoading = false;
    });
  }

  // Create a story
  create(oneliner: HTMLInputElement): void {
    if (oneliner.value) {
      this.loadingService.load();
      const story = new Story();
      story.setOneliner(oneliner.value);
      story.setIsShared(false);
      this.firebaseService.createStory(story).subscribe(id => {
        this.router.navigate(['/story', id]);
      });
    } else {
      this.notificationService.error('Empty story can not be added');
    }
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}
