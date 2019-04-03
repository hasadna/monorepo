import { Component, Input } from '@angular/core';

import { Screenshot, Story, StoryList, User } from '@/core/proto';

@Component({
  selector: 'app-story-list-contributor',
  templateUrl: './story-list-contributor.component.html',
  styleUrls: ['./story-list-contributor.component.scss']
})
export class StoryListContributorComponent {

  @Input() users: User[];
  @Input() user: User[];
  @Input() storyList: StoryList[];
  @Input() screenshots: Screenshot[];

  getRouterLink(story: Story, screenshot: Screenshot): string {
    const id: string = story.getId();
    const filename: string = screenshot.getFilename();
    const author: string = story.getAuthor();
    return `/single-screenshot/${id}/${filename}/${author}`;
  }
}
