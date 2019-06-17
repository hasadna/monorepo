import { Component, Input, Output, EventEmitter } from '@angular/core';

import { HeaderService } from '@/core/services';

@Component({
  selector: 'project-select',
  templateUrl: './project-select.component.html',
  styleUrls: ['./project-select.component.scss'],
})
export class ProjectSelectComponent {
  @Input() projectIdList: string[];
  @Output() selectEmitter = new EventEmitter<string>();

  constructor(public headerService: HeaderService) { }

  select(projectId: string): void {
    this.headerService.selectedProjectId = projectId;
    this.selectEmitter.emit(projectId);
  }
}
