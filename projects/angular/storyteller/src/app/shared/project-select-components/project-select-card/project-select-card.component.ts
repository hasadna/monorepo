import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'project-select-card',
  templateUrl: './project-select-card.component.html',
  styleUrls: ['./project-select-card.component.scss'],
})
export class ProjectSelectCardComponent {
  @Input() projectIdList: string[];
  @Output() selectEmitter = new EventEmitter<string>();
}
