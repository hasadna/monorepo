import {Project} from './project';
import {Contribution} from './contribution';

export class User {
    userId: number;
    userName: string;
    firstName: string;
    lastName: string;
    email: string;
    socialNetworks: string[];
    skills: string[];
    projects: number[];
    userContributions: Contribution[];
    userImg: string;
}
