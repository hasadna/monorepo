import { Component, OnInit } from '@angular/core';

import { User } from '@/proto';
import { FirebaseService } from '@/services';

@Component({
  selector: 'app-contacts',
  templateUrl: './contacts.component.html',
  styleUrls: ['./contacts.component.scss']
})
export class ContactsComponent implements OnInit {
  users: User[];

  constructor(private firebaseService: FirebaseService) { }

  ngOnInit() {
    this.firebaseService.getUserList()
      .subscribe(userList => {
        this.users = userList;
      });
  }
}
