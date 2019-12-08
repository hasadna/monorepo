import { Component, OnInit, OnDestroy, Input, Output, EventEmitter } from '@angular/core';
import { FormControl } from '@angular/forms';
import { Subscription } from 'rxjs';

import { Story } from '@/core/proto';
import {
  UserService,
  FirebaseService,
  AuthService,
} from '@/core/services';

@Component({
  selector: 'story-select',
  templateUrl: './story-select.component.html',
  styleUrls: ['./story-select.component.scss'],
})
export class StorySelectComponent implements OnInit, OnDestroy {
  selectedStory: Story;
  storySelect = new FormControl();
  onelinerList: string[] = [];
  storySub = new Subscription();
  @Input() story: Story;
  @Output() selectStory = new EventEmitter<Story>();
  @Output() valueChanges = new EventEmitter<void>();
  @Output() createStory = new EventEmitter<Story>();

  constructor(
    public userService: UserService,
    private firebaseService: FirebaseService,
    private authService: AuthService,
  ) {
    this.storySub = this.firebaseService.getStoryList(this.authService.email).subscribe(() => {
      this.updateOnelinerList();
      // Check if selected story has deleted
      if (this.selectedStory) {
        const idList: string[] = this.userService.storyList.map(story => story.getId());
        if (!idList.includes(this.selectedStory.getId())) {
          // Looks like selected story was deleted. Unselect it
          this.selectedStory = undefined;
          this.storySelect.setValue('', { emitEvent: false });
          this.updateOnelinerList();
          this.valueChanges.emit();
        }
      }
    });

    // When story is selected by UI
    this.storySelect.valueChanges.subscribe((oneliner: string) => {
      this.valueChanges.emit();
      this.updateOnelinerList();
      if (oneliner) {
        for (const story of this.userService.storyList) {
          if (story.getOneliner() === oneliner) {
            this.selectedStory = story;
            this.selectStory.emit(story);
            break;
          }
        }
      }
    });
  }

  ngOnInit() {
    if (this.story) {
      this.selectedStory = this.story;
      this.storySelect.setValue(this.story.getOneliner(), { emitEvent: false });
    }
  }

  updateOnelinerList(): void {
    // Show last 10 options, which include user input
    let storyList: Story[] = this.userService.storyList.slice();
    storyList.sort((a, b) => {
      return Math.sign(b.getStartedMs() - a.getStartedMs());
    });
    if (this.storySelect.value) {
      storyList = storyList.filter(story =>
        story.getOneliner().toLowerCase().includes(this.storySelect.value.toLowerCase()),
      );
    }
    storyList = storyList.slice(0, Math.min(10, storyList.length));
    this.onelinerList = storyList.map(story => story.getOneliner());
  }

  createNewStory(oneliner: string): void {
    const story = new Story();
    story.setOneliner(oneliner);
    this.createStory.emit(story);
  }

  ngOnDestroy() {
    this.storySub.unsubscribe();
  }
}
