import { User } from './user';
import { Contribution } from './contribution';

export class Project {
  projectId: number;
  projectName: string;
  projectDescription: string;
  projectSite: string;
  contributors: number[];
  projectContributions: Contribution[];
}
