import { Injectable } from '@angular/core';

import {Observable, of} from 'rxjs';

import {User} from './user';
import {Project} from './project';

import {USERS} from './mock-users';
import {PROJECTS} from './mock-projects';

@Injectable({
  providedIn: 'root'
})
export class DataService {

  constructor() { }

  getAllUsers(): Observable<User[]> {
    return of(USERS);
  }

  getAllProjects(): Observable<Project[]> {
    return of(PROJECTS);
  }
}
