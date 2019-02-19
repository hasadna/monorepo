import { Injectable } from '@angular/core';
import { StoryItem, Screenshot } from '../proto';

@Injectable()
export class StoryService {
  private data = [];


  setData(sItem:StoryItem, screenshot: Screenshot):void{
    this.data[0] = sItem;
    this.data[1] = screenshot;

  }
  getData(): any {
    return this.data;
  }
}
