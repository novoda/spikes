import { Injectable } from '@angular/core';

/// <reference path="jstz.d.ts" />
import * as jstz from 'jstz';

@Injectable()
export class TimezoneDetectorService {

  constructor() {
  }

  getTimezone(): string {
    const timezone = jstz.determine();
    return timezone.name();
  }

}
