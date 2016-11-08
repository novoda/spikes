/* tslint:disable:no-unused-variable */

import { addProviders, inject } from '@angular/core/testing';
import { ContributorsDashboardComponent } from './contributors-dashboard.component';
import { SystemClock } from '../system-clock';
import { WeekCalculator } from '../week-calculator.service';
import { ReportsService } from '../reports/reports.service';
import { ReportsClient } from '../reports/reports-client.service';
import { UserStats } from '../reports/user-stats';
import { CompanyStats } from '../reports/company-stats';
import { Observable, Scheduler } from 'rxjs';
import { ReportsMockService } from '../reports/reports-mock.service';
import { TimezoneDetectorService } from '../timezone-detector.service';

describe('Component: ContributorsDashboard', () => {

  let clock: SystemClock;
  let weekCalculator: WeekCalculator;
  let reportsService: ReportsMockService;
  let reportsServiceClient: ReportsClient;
  let timezoneDetectorService: TimezoneDetectorService;

  let component: ContributorsDashboardComponent;

  beforeEach(() => {
    addProviders([
      SystemClock,
      WeekCalculator,
      {
        provide: ReportsService,
        useClass: ReportsMockService
      },
      ReportsClient,
      TimezoneDetectorService
    ]);
  });

  beforeEach(inject([SystemClock, WeekCalculator, ReportsService, ReportsClient, TimezoneDetectorService],
    (_clock_, _weekCalculator_, _reportsService_, _reportsServiceClient_, _timezoneDetectorService_) => {
      clock = _clock_;
      weekCalculator = _weekCalculator_;
      reportsService = _reportsService_;
      reportsServiceClient = _reportsServiceClient_;
      timezoneDetectorService = _timezoneDetectorService_;
    }));

  beforeEach(() => {
    component = new ContributorsDashboardComponent(
      weekCalculator,
      clock,
      reportsServiceClient,
      timezoneDetectorService
    );
  });

  let companyStats: CompanyStats;

  const newUserStats = (username: string): UserStats => {
    return new UserStats(username, null, null, null);
  };

  beforeEach(() => {
    const teamContributors = [
      newUserStats('banana'),
      newUserStats('joe'),
      newUserStats('trinity'),
      newUserStats('bud'),
      newUserStats('boss'),
      newUserStats('goku')
    ];

    const projectContributors = [
      newUserStats('blundell'),
      newUserStats('xavi'),
      newUserStats('frapontillo'),
      newUserStats('takecare')
    ];

    companyStats = new CompanyStats(teamContributors, projectContributors);
  });

  it('creates an instance', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {

    it('subscribes to the service', () => {
      component.ngOnInit();

      expect(component.subscription).toBeTruthy();
    });


    // TODO: temporarily deactivated due to TestScheduler lack of doc (https://github.com/ReactiveX/rxjs/issues/1791)
    xit('gets the company stats', () => {
      component.ngOnInit();

      Scheduler.async.flush();

      expect(timezoneDetectorService.getTimezone).toHaveBeenCalled();
      expect(reportsService.getAggregatedStats).toHaveBeenCalled();
    });

    xit('refreshes statistics every 30 seconds', () => {
      // TODO: add tests wrt refresh of statistics (https://github.com/ReactiveX/rxjs/issues/1791)
    });

    // TODO: fix refresh test with proper scheduelr (https://github.com/ReactiveX/rxjs/issues/1791)
    xit('retries the get statistics operation when it fails', () => {
      const failTimes = 3;
      let failCounter = 0;

      spyOn(reportsService, 'getAggregatedStats').and.callFake((): any => {
        if (failCounter < failTimes) {
          failCounter += 1;
          return Observable.throw(new Error('Some network error'));
        }

        return Observable.from([{
          'usersStats': {
            'tasomaniac': {
              'assignedProjectsStats': {},
              'assignedProjectsContributions': 0,
              'externalRepositoriesStats': {},
              'externalRepositoriesContributions': 0
            }
          }
        }]);
      });

      component.ngOnInit();

      component.subscription
        .add(() => {
          expect(reportsService.getAggregatedStats).toHaveBeenCalledTimes(failTimes + 1);
        });
    });

    it('sets teamContributors and projectContributors', () => {
      component.ngOnInit();

      component.subscription
        .add(() => {
          expect(component.teamContributors).toBeDefined();
          expect(component.projectContributors).toBeDefined();
        });
    });

  });

  describe('ngOnDestroy', () => {

    it('unsubscribes from the service', () => {
      component.ngOnInit();

      component.ngOnDestroy();

      expect(component.subscription.closed).toBe(true);
    });

    it('does not unsubscribe from the service if it was already unsubscribed', () => {
      component.ngOnInit();
      component.subscription.unsubscribe();

      spyOn(component.subscription, 'unsubscribe');
      component.ngOnDestroy();
      expect(component.subscription.unsubscribe).not.toHaveBeenCalled();
    });

  });

});
