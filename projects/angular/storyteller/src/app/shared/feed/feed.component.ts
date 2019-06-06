import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { Subscription, Subject } from 'rxjs';

import { ScrollService, ScreenService, HeaderService } from '@/core/services';
import { EasyStory } from '@/shared';

const DEFAULT_STORIES_AMOUNT = 4;

@Component({
  selector: 'story-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.scss'],
})
export class FeedComponent implements OnInit, OnDestroy {
  displayedAmount: number = DEFAULT_STORIES_AMOUNT;
  scrollSubscription = new Subscription();
  filterSubscription = new Subscription();
  filteredEasyStories: EasyStory[];
  displayedEasyStories: EasyStory[];
  @Input() easyStories: EasyStory[];
  @Input() filterChanges: Subject<string>;

  constructor(
    private scrollService: ScrollService,
    private screenService: ScreenService,
    private headerService: HeaderService,
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
    this.filteredEasyStories = this.easyStories.slice();
    this.displayStories();
    this.filtersStories(this.headerService.selectedProjectId);
    this.filterSubscription = this.filterChanges.subscribe(projectId => {
      this.filtersStories(projectId);
    });
  }

  displayMorePersons(): void {
    this.displayedAmount += DEFAULT_STORIES_AMOUNT;
    this.displayStories();
  }

  // Filters stories by project id
  private filtersStories(projectId: string): void {
    this.displayedAmount = DEFAULT_STORIES_AMOUNT;
    this.filteredEasyStories = (projectId !== 'all') ?
      this.easyStories.filter(story => story.project === projectId) :
      this.easyStories;
    this.displayStories();
  }

  // Puts limited about of stories to a variable, which is used in html template
  private displayStories(): void {
    this.displayedAmount = Math.min(this.displayedAmount, this.filteredEasyStories.length);
    this.displayedEasyStories = this.filteredEasyStories.slice(0, this.displayedAmount);
  }

  ngOnDestroy() {
    this.scrollSubscription.unsubscribe();
    this.filterSubscription.unsubscribe();
  }
}
