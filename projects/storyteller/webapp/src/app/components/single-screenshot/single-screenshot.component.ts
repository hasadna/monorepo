import { Component, OnInit, Input } from '@angular/core';
import { ScreenshotMetadata } from '@/core/proto';

@Component({
  selector: 'app-single-screenshot',
  templateUrl: './single-screenshot.component.html',
  styleUrls: ['./single-screenshot.component.scss']
})
export class SingleScreenshotComponent implements OnInit {

  constructor() { }

  @Input() storyItemSingle: string;

  ngOnInit() {
  }

}
