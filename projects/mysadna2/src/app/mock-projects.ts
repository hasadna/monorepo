import {Project} from './project';
import {User} from './user';
import {Contribution} from './contribution';

export const PROJECTS: Project[] = [
    {projectId: 1, projectName: "OpenTrain", projectDescription: "OpenTrain takes Israel Railways train data such as arrival times, analyzes it and makes it available for the public.", projectSite: "https://github.com/hasadna/OpenTrainCommunity", contributors: [1, 2, 3], projectContributions:["Added profile pages and project pages.", "Added profile and project pages.", "Added profile pages and project pages."]},
    {projectId: 2, projectName: "OpenKnesset", projectDescription: "OpenKnesset provides data about the goings on in the different offices in the Knesset.", projectSite: "https://github.com/hasadna/OpenKnessetCommunity", contributors: [1, 2], projectContributions: []},
    {projectId: 3, projectName: "OpenPension", projectDescription: "OpenPension offers a look into what is being done with the public's pension funds.", projectSite: "https://github.com/hasadna/OpenPensionCommunity", contributors: [2, 3], projectContributions: []}
];