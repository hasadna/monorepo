import { Component, Input } from '@angular/core';

import { User } from '@/proto';

@Component({
  selector: 'app-contributor',
  templateUrl: './contributor.component.html',
  styleUrls: ['./contributor.component.scss']
})
export class ContributorComponent {
  @Input() user: User;
}
