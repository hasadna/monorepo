import { Component, OnInit } from '@angular/core';

import {User} from '../models/user';
import {DataService} from '../services/data.service';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss']
})
export class UsersComponent implements OnInit {

  users: User[];

  constructor(private dataService: DataService) { }

  ngOnInit() {
    this.getAllUsers();
  }

  getAllUsers(): void {
    this.dataService.getAllUsers()
      .subscribe(users => this.users = users);
  }
}
