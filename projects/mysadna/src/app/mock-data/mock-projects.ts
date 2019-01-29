import { Project } from '../models/project';
import { User } from '../models/user';
import { Contribution } from '../models/contribution';

export const PROJECTS: Project[] = [
  {
    projectId: 1,
    projectName: 'Open Train',
    // tslint:disable-next-line
    projectDescription: 'Open Train takes Israel Railways train data such as arrival times, analyzes it and makes it available for the public.',
    projectSite: 'https://github.com/hasadna/OpenTrainCommunity',
    contributors: [1, 2, 3],
    projectContributions: [
      {
        contributionId: 1,
        userId: 1, projectId: 1,
        contributionMessage: 'Added profile pages and project pages.',
        contributionUrl: 'https://github.com/proj/demo/mysadna/commit/34ed7598d30022b798a75b375eb254a1f0682ecg'
      },
      {
        contributionId: 2,
        userId: 1,
        projectId: 1,
        contributionMessage: 'Added profile and project pages.',
        contributionUrl: 'https://github.com/proj/demo/mysadna/commit/34ed7598d30022b798a75b375eb254a1f0682ecg'
      },
      {
        contributionId: 4,
        userId: 1,
        projectId: 1,
        contributionMessage: 'Added profile pages and project pages.',
        contributionUrl: 'https://github.com/proj/demo/mysadna/commit/34ed7598d30022b798a75b375eb254a1f0682ecg'
      }
    ]
  },
  {
    projectId: 2,
    projectName: 'Open Knesset',
    projectDescription: 'Open Knesset provides data about the goings on in the different offices in the Knesset.',
    projectSite: 'https://github.com/hasadna/OpenKnessetCommunity',
    contributors: [1, 2],
    projectContributions: [

    ]
  },
  {
    projectId: 3,
    projectName: 'Open Pension',
    projectDescription: 'Open Pension offers a look into what is being done with the public\'s pension funds.',
    projectSite: 'https://github.com/hasadna/OpenPensionCommunity',
    contributors: [2, 3],
    projectContributions: [

    ]
  }
];
