import { Story, StoryItem, User } from '@/core/proto';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-story-headline',
  templateUrl: './story-headline.component.html',
  styleUrls: ['./story-headline.component.scss'],
})
export class StoryHeadlineComponent {
  @Input() user: User;
  @Input() story: Story;
  @Input() storyItem: StoryItem;
}
