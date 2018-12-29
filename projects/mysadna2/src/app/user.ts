import {Project} from './project';

export class User {
    userId: number;
    userName: string;
    firstName: string;
    lastName: string;
    email: string;
    socialNetworks: [string];
    skills: [string];
    projects: [Project];
    contributions: [string];
}
