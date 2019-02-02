export * from './data.service';
export * from './encoding.service';
export * from './firebase.service';

import { DataService } from './data.service';
import { EncodingService } from './encoding.service';
import { FirebaseService } from './firebase.service';

export const ServiceList = [
  DataService,
  EncodingService,
  FirebaseService,
];
