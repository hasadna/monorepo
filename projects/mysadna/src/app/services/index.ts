export * from './encoding.service';
export * from './firebase.service';

import { EncodingService } from './encoding.service';
import { FirebaseService } from './firebase.service';

export const ServiceList = [
  EncodingService,
  FirebaseService
];
