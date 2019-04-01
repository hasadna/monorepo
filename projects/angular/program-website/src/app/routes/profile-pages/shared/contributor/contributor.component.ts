import { Component, Input } from '@angular/core';

import { User } from '@/core/proto';

@Component({
  selector: 'profile-pages-contributor',
  templateUrl: './contributor.component.html',
  styleUrls: ['./contributor.component.scss'],
})
export class ContributorComponent {
  @Input() user: User;
}
