import { Component, OnInit } from '@angular/core';

import {Project} from '../../models/project';
import {DataService} from '../../services/data.service';

@Component({
  selector: 'app-projects',
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.scss']
})
export class ProjectsComponent implements OnInit {

  projects: Project[];

  constructor(private dataService: DataService) { }

  ngOnInit() {
    this.getAllProjects();
  }

  getAllProjects(): void {
    this.dataService.getAllProjects()
      .subscribe(projects => this.projects = projects);
  }
}
