import { Component, OnInit } from '@angular/core';

import { User } from '@/core/proto';
import { FirebaseService } from '@/core/services';

@Component({
  selector: 'profile-pages-contacts',
  templateUrl: './contacts.component.html',
  styleUrls: ['./contacts.component.scss'],
})
export class ContactsComponent implements OnInit {
  users: User[];

  constructor(private firebaseService: FirebaseService) { }

  ngOnInit() {
    this.firebaseService.getReviewerConfig()
      .subscribe(reviewerConfig => {
        this.users = reviewerConfig.getUserList() ;
      });
  }
}
