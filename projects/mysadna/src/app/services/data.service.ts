import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';

import { User } from '@/models/user';
import { Project } from '@/models/project';
import { USERS } from '@/mock-data/mock-users';
import { PROJECTS } from '@/mock-data/mock-projects';

@Injectable()
export class DataService {
  // GET users from the server (currently local const USERS)
  getAllUsers(): Observable<User[]> {
    return of(USERS);
  }

  // GET projects from the server (currently local const PROJECTS)
  getAllProjects(): Observable<Project[]> {
    return of(PROJECTS);
  }

  // GET User by userId.
  getUser(userId: number): Observable<User> {
    return of(USERS.find(user => user.userId === userId));
  }

  // GET Projects by userId.
  getUserProjects(userId: number): Observable<Project[]> {
    return of(PROJECTS.filter((project) => project.contributors.includes(userId)));
  }

  // GET Project by projectId.
  getProject(projectId: number): Observable<Project> {
    return of(PROJECTS.find(project => project.projectId === projectId));
  }

  // GET Users by projectId.
  getProjectUsers(projectId: number): Observable<User[]> {
    return of(USERS.filter((user) => user.projects.includes(projectId)));
  }
}
