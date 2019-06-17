import { Component, Input, Output, EventEmitter } from '@angular/core';

import { HeaderService } from '@/core/services';

@Component({
  selector: 'project-select-popup',
  templateUrl: './project-select-popup.component.html',
  styleUrls: ['./project-select-popup.component.scss'],
})
export class ProjectSelectPopupComponent {
  @Input() projectIdList: string[];
  @Output() selectEmitter = new EventEmitter<string>();

  constructor(private headerService: HeaderService) { }

  select(projectId: string): void {
    this.headerService.selectedProjectId = projectId;
    this.headerService.toggle();
    this.selectEmitter.emit(projectId);
  }
}
