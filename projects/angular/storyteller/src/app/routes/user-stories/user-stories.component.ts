import { Component } from '@angular/core';

import { Story } from '@/core/proto';
import {
  LoadingService,
  UserService,
  FirebaseService,
  NotificationService,
} from '@/core/services';

@Component({
  selector: 'page-user-stories',
  templateUrl: './user-stories.component.html',
  styleUrls: ['./user-stories.component.scss'],
})
export class UserStoriesComponent {
  constructor(
    public loadingService: LoadingService,
    public userService: UserService,
    public firebaseService: FirebaseService,
    public notificationService: NotificationService,
  ) {
    this.loadingService.isLoading = false;
  }

  deleteStory(story: Story): void {
    if (confirm(`Delete: "${story.getOneliner()}"?`)) {
      this.firebaseService.removeStory(story).subscribe(() => {
        this.notificationService.success('Story is deleted');
      });
    }
  }
}
