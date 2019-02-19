import { Component, OnInit } from '@angular/core';
import { StoryItem, Screenshot } from '@/core/proto';
import { StoryService } from '@/core/services/story.service';

@Component({
  selector: 'app-single-screenshot',
  templateUrl: './single-screenshot.component.html',
  styleUrls: ['./single-screenshot.component.scss']
})
export class SingleScreenshotComponent implements OnInit {
  story:StoryItem;
  screenshot: Screenshot;
  data = [];
  ;

  constructor(storyservice:StoryService) {
    this.data = storyservice.getData();
    this.story = this.data[0];
    this.screenshot = this.data[1];
   }



  ngOnInit() {
  }
}
