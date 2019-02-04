import { Component, OnInit } from '@angular/core';

import { User } from '@/models/user';
import { DataService } from '@/services/data.service';

@Component({
  selector: 'app-contacts',
  templateUrl: './contacts.component.html',
  styleUrls: ['./contacts.component.scss']
})
export class ContactsComponent implements OnInit {
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
