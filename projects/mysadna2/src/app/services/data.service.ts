import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';

import { User } from '../models/user';
import { Project } from '../models/project';

import {USERS} from '../mock-data/mock-users';
import {PROJECTS} from '../mock-data/mock-projects';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application.json' })
};

@Injectable({
  providedIn: 'root'
})
export class DataService {
  // private usersUrl = 'api/users'; //For web api
  // private projectsUrl = 'api/projects';

  constructor() { }
// private http: HttpClient
  /**
  * Handle Http operation that failed.
  * Let the app continue.
  * @param operation - name of the operation that failed
  * @param result - optional value to return as the observable result
  */
  private handleError<T> (operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead

      // TODO: better job of transforming error for user consumption
      console.log(`${operation} failed: ${error.message}`);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }

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
