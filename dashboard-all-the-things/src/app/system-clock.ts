import { Injectable } from '@angular/core';

@Injectable()
export class SystemClock {

  getDate(): Date {
    return new Date();
  }

}
