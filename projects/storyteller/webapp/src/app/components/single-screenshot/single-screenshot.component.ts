import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { StoryService } from '@/core/services';
import { Story, Screenshot } from '@/core/proto';

@Component({
  selector: 'app-single-screenshot',
  templateUrl: './single-screenshot.component.html',
  styleUrls: ['./single-screenshot.component.scss']
})
export class SingleScreenshotComponent implements OnInit {
  storyid: string;
  screenshot_filename: string;
  story: Story;
  screenshot: Screenshot;
  constructor(private activatedRoute: ActivatedRoute, private storyService: StoryService) { }
  ngOnInit() {
    this.storyid = this.activatedRoute.snapshot.params['sotryId'];
    this.story = this.storyService.getStory(this.storyid);
    this.screenshot_filename = this.activatedRoute.snapshot.params['screenshot'];
    this.screenshot = this.storyService.getScreenshot(this.screenshot_filename);
  }
}
