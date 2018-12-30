import {User} from './user';
import {Project} from './project';
import {Contribution} from './contribution';

export const USERS: User[] = [
    {userId: 1, userName: "litalm", firstName: "Lital", lastName: "Morgenstein", email: "lital.morg@gmail.com", socialNetworks: ["LinkedIn", "Github", "Twitter", "Facebook"], skills: ["Java", "swift", "dart", "C++", "bash", "Front-End development", "Mobile development", "Flutter", "Android", "iOS", "Android Studio", "Xcode", "Linux", "mac"], projects: [1, 2], userContributions: ["Added profile pages and project pages.", "Added profile and project pages.", "Added profile pages and project pages."]},
    {userId: 2, userName: "litalm2", firstName: "Lital2", lastName: "Morgenstein2", email: "lital.morg2@gmail.com", socialNetworks: ["LinkedIn", "Github"], skills: ["Java", "C++"], projects: [1, 2, 3], userContributions: []},
    {userId: 3, userName: "litalm3", firstName: "Lital3", lastName: "Morgenstein3", email: "lital.morg3@gmail.com", socialNetworks: ["Github", "Facebook"], skills: ["swift", "dart"], projects: [1, 3], userContributions: []}
];