import { Component, OnInit, OnDestroy } from '@angular/core';
import { SystemClock } from '../system-clock';
import { WeekCalculator } from '../week-calculator.service';
import { ReportsClient } from '../reports/reports-client.service';
import { CompanyStats } from '../reports/company-stats';
import { UserStats } from '../reports/user-stats';
import { Subscription, Observable } from 'rxjs';
import { OnErrorIgnoreOperator } from '../../../shared/OnErrorIgnoreOperator';
import { TimezoneDetectorService } from '../timezone-detector.service';

@Component({
  selector: 'contributors-dashboard',
  templateUrl: 'contributors-dashboard.component.html',
  styleUrls: ['contributors-dashboard.component.scss']
})
export class ContributorsDashboardComponent implements OnInit, OnDestroy {

  static REFRESH_RATE_IN_MILLISECONDS = 30 * 1000;

  public teamContributors: Array<UserStats>;
  public projectContributors: Array<UserStats>;
  public subscription: Subscription;

  constructor(private weekCalculator: WeekCalculator,
              private clock: SystemClock,
              private reportsServiceClient: ReportsClient,
              private timezoneDetector: TimezoneDetectorService) {
    // noop
  }

  ngOnInit() {
    this.subscription = Observable
      .timer(0, ContributorsDashboardComponent.REFRESH_RATE_IN_MILLISECONDS)
      .switchMap(() => {
        return this.getCompanyStats();
      })
      .subscribe((stats: CompanyStats) => {
        this.teamContributors = stats.contributors;
        this.projectContributors = stats.slackers;
      });
  }

  private getCompanyStats(): Observable<CompanyStats> {
    return this.reportsServiceClient
      .getCompanyStats(
        this.weekCalculator.getLastMonday(),
        this.clock.getDate(),
        this.timezoneDetector.getTimezone()
      )
      .lift(new OnErrorIgnoreOperator<CompanyStats>());
  }

  ngOnDestroy(): void {
    if (!this.subscription.closed) {
      this.subscription.unsubscribe();
    }
  }

}
