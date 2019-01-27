import { User } from '../models/user';
import { Project } from '../models/project';
import { Contribution } from '../models/contribution';

export const USERS: User[] = [
  {
    userId: 1,
    userName: 'litalm',
    firstName: 'Lital',
    lastName: 'Morgenstein',
    email: 'lital.morg@gmail.com',
    socialNetworks: [
      'LinkedIn',
      'Github',
      'Twitter',
      'Facebook'
    ],
    skills: [
      'Java',
      'swift',
      'dart',
      'C++',
      'bash',
      'Front-End development',
      'Mobile development',
      'Flutter',
      'Android',
      'iOS',
      'Android Studio',
      'Xcode',
      'Linux',
      'mac'
    ],
    projects: [1, 2],
    userContributions: [
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
    ],
    userImg: './assets/images/default-avatar.svg'
  },
  {
    userId: 2,
    userName: 'litalm2',
    firstName: 'Lital2',
    lastName: 'Morgenstein2',
    email: 'lital.morg2@gmail.com',
    socialNetworks: [
      'LinkedIn',
      'Github'
    ],
    skills: [
      'Java',
      'C++'
    ],
    projects: [1, 2, 3],
    userContributions: [

    ],
    userImg: './assets/images/default-avatar.svg'
  },
  {
    userId: 3,
    userName: 'litalm3',
    firstName: 'Lital3',
    lastName: 'Morgenstein3',
    email: 'lital.morg3@gmail.com',
    socialNetworks: [
      'Github',
      'Facebook'
    ],
    skills: [
      'swift',
      'dart'
    ],
    projects: [1, 3],
    userContributions: [

    ],
    userImg: './assets/images/default-avatar.svg'
  }
];
