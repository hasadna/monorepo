import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'project-select',
  templateUrl: './project-select.component.html',
  styleUrls: ['./project-select.component.scss'],
})
export class ProjectSelectComponent {
  selectedProjectId: string = 'all';
  @Input() projectIdList: string;
  @Output() selectEmitter = new EventEmitter<string>();

  select(projectId: string): void {
    this.selectedProjectId = projectId;
    this.selectEmitter.emit(projectId);
  }
}
