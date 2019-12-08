import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { Subscription, Subject } from 'rxjs';

import { EasyStory } from '@/core/interfaces';
import { ScrollService, ScreenService } from '@/core/services';

const DEFAULT_STORIES_AMOUNT = 4;

@Component({
  selector: 'app-stories',
  templateUrl: './stories.component.html',
  styleUrls: ['./stories.component.scss'],
})
export class StoriesComponent implements OnInit, OnDestroy {
  displayedAmount: number = DEFAULT_STORIES_AMOUNT;
  scrollSubscription = new Subscription();
  displayedEasyStories: EasyStory[];
  @Input() easyStories: EasyStory[];
  @Input() filterChanges: Subject<string>;

  constructor(
    private scrollService: ScrollService,
    private screenService: ScreenService,
  ) {
    // Detect user scrolling
    this.scrollSubscription = this.scrollService.scroll.subscribe(scrollTop => {
      const scrollHeight: number = this.scrollService.scrollHeight - this.screenService.height;
      const redLine: number = scrollHeight - 200;
      if (scrollTop >= redLine) {
        // Display more persons, if the bottom of the screen is reached
        this.displayMorePersons();
      }
    });
  }

  ngOnInit() {
    this.displayStories();
  }

  displayMorePersons(): void {
    this.displayedAmount += DEFAULT_STORIES_AMOUNT;
    this.displayStories();
  }

  // Puts limited amount of stories to a variable, which is used in html template
  private displayStories(): void {
    this.displayedAmount = Math.min(this.displayedAmount, this.easyStories.length);
    this.displayedEasyStories = this.easyStories.slice(0, this.displayedAmount);
  }

  ngOnDestroy() {
    this.scrollSubscription.unsubscribe();
  }
}
