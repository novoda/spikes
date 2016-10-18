import { Injectable } from '@angular/core';
import { SystemClock } from './system-clock';

@Injectable()
export class WeekCalculator {

  private static MILLIS_IN_DAY: number = 24 * 60 * 60 * 1000;

  constructor(private clock: SystemClock) {
  }

  getLastMonday(): Date {
    let nowDate: Date = this.clock.getDate();
    let dayOfWeek = nowDate.getDay() ? nowDate.getDay() : 7;
    let daysOffsetInMillis = (dayOfWeek - 1) * WeekCalculator.MILLIS_IN_DAY;
    let lastMonday = new Date(nowDate.getTime() - daysOffsetInMillis);

    lastMonday.setHours(0);
    lastMonday.setMinutes(0);
    lastMonday.setSeconds(0);
    lastMonday.setMilliseconds(0);

    return lastMonday;
  }

}
