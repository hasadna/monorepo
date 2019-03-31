export * from './project-details';
export * from './projects';
export * from './user-details';
export * from './contacts';

import { ContactsComponent } from './contacts';
import { ProjectDetailsComponent } from './project-details';
import { ProjectsComponent } from './projects';
import { UserDetailsComponent } from './user-details';

export const ComponentList = [
  ProjectDetailsComponent,
  ProjectsComponent,
  UserDetailsComponent,
  ContactsComponent
];
