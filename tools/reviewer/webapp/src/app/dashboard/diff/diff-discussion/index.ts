export * from './diff-discussion.component';
export * from './code-threads';
export * from './diff-threads';
export * from './discussion.service';

// Components
import { CodeThreadsComponent } from './code-threads';
import { DiffDiscussionComponent } from './diff-discussion.component';
import { DiffThreadsComponent } from './diff-threads';
export const DiffDiscussionComponentList = [
  DiffDiscussionComponent,
  CodeThreadsComponent,
  DiffThreadsComponent,
];

// Services
import { ThreadStateService } from './thread-state.service';
export const DiffDiscussionServiceList = [
  ThreadStateService,
];
