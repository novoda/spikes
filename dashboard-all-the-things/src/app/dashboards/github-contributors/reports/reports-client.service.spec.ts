/* tslint:disable:no-unused-variable */

import { addProviders } from '@angular/core/testing';
import { ReportsService } from './reports.service';
import { inject } from '@angular/core/testing/test_bed';
import { ReportsMockService } from './reports-mock.service';
import { ReportsClient } from './reports-client.service';
import { Observable } from 'rxjs';
import { async } from '@angular/core/testing/async';
import { CompanyStats } from './company-stats';
import { UserStats } from './user-stats';

describe('Service: ReportsClient', () => {

  let service: ReportsService;
  let client: ReportsClient;
  const ANY_DATE: Date = new Date();
  const ANY_TIMEZONE = 'Europe/London';

  beforeEach(() => {
    addProviders([
      {
        provide: ReportsService,
        useClass: ReportsMockService
      },
      ReportsClient
    ]);
  });

  beforeEach(inject([ReportsService, ReportsClient], (_service_: ReportsService, _client_: ReportsClient) => {
    service = _service_;
    client = _client_;
  }));

  const givenStats = (stats: {usersStats: any}) => {
    spyOn(service, 'getAggregatedStats').and.returnValue(
      Observable.from([stats]));
  };

  it('instantiates the client', () => {
    expect(client).toBeTruthy();
  });

  it('does not include in stats people with no assigned contributions', () => {
    givenStats({
      'usersStats': {
        'frapontillo': {
          'assignedProjectsContributions': 0,
          'externalRepositoriesContributions': 123
        },
        'takecare': {
          'assignedProjectsContributions': 567,
          'externalRepositoriesContributions': 456
        }
      }
    });

    client.getCompanyStats(ANY_DATE, ANY_DATE, ANY_TIMEZONE)
      .subscribe((actual: CompanyStats) => {
        let expected: CompanyStats = new CompanyStats(
          [new UserStats('takecare', jasmine.any(String), 567, 456)],
          []
        );

        expect(actual).toEqual(expected);
      });
  });

  it('sets as contributors all people with > 0 external contributions and > 0 assigned contributions', async(() => {
    givenStats({
      'usersStats': {
        'frapontillo': {
          'assignedProjectsContributions': 234,
          'externalRepositoriesContributions': 123
        },
        'takecare': {
          'assignedProjectsContributions': 567,
          'externalRepositoriesContributions': 456
        }
      }
    });

    client.getCompanyStats(ANY_DATE, ANY_DATE, ANY_TIMEZONE)
      .subscribe((actual: CompanyStats) => {
        let expected: UserStats[] = [
          new UserStats('frapontillo', jasmine.any(String), 234, 123),
          new UserStats('takecare', jasmine.any(String), 567, 456)
        ];

        expect(actual.contributors).toEqual(expected);
      });
  }));

  it('sets as projectContributors all people with 0 external contributions and > 0 assigned contributions', async(() => {
    givenStats({
      'usersStats': {
        'frapontillo': {
          'assignedProjectsContributions': 234,
          'externalRepositoriesContributions': 0
        },
        'takecare': {
          'assignedProjectsContributions': 567,
          'externalRepositoriesContributions': 0
        }
      }
    });

    client.getCompanyStats(ANY_DATE, ANY_DATE, ANY_TIMEZONE)
      .subscribe((actual: CompanyStats) => {
        let expected: UserStats[] = [
          new UserStats('frapontillo', jasmine.any(String), 234, 0),
          new UserStats('takecare', jasmine.any(String), 567, 0)
        ];

        expect(actual.slackers).toEqual(expected);
      });
  }));

  it('concatenates assigned projects with a comma', async(() => {
    givenStats({
      'usersStats': {
        'alexstyl': {
          'assignedProjectsStats': {'OddsChecker': 128, 'Something': 128},
          'assignedProjectsContributions': 256,
          'externalRepositoriesStats': {'all-4': 8, 'spikes': 15},
          'externalRepositoriesContributions': 23
        },
        'florianmski': {
          'assignedProjectsStats': {'Creators': 161, 'Another': 1},
          'assignedProjectsContributions': 162,
          'externalRepositoriesStats': {},
          'externalRepositoriesContributions': 0
        },
      }
    });

    client.getCompanyStats(ANY_DATE, ANY_DATE, ANY_TIMEZONE)
      .subscribe((actual: CompanyStats) => {
        let expected: CompanyStats = new CompanyStats(
          [new UserStats('alexstyl', 'OddsChecker, Something', jasmine.any(Number), jasmine.any(Number))],
          [new UserStats('florianmski', 'Creators, Another', jasmine.any(Number), jasmine.any(Number))]
        );

        expect(actual).toEqual(expected);
      });
  }));

  it('removes "Verified" and "Scheduled" from project names when concatenating', async(() => {
    givenStats({
      'usersStats': {
        'alexstyl': {
          'assignedProjectsStats': {'OddsChecker: Verified': 128, 'Something: Scheduled': 128},
          'assignedProjectsContributions': 256,
          'externalRepositoriesStats': {'all-4': 8, 'spikes': 15},
          'externalRepositoriesContributions': 23
        },
        'florianmski': {
          'assignedProjectsStats': {'Creators: Scheduled': 161, 'Another: Verified': 1},
          'assignedProjectsContributions': 162
        },
      }
    });

    client.getCompanyStats(ANY_DATE, ANY_DATE, ANY_TIMEZONE)
      .subscribe((actual: CompanyStats) => {
        let expected: CompanyStats = new CompanyStats(
          [new UserStats('alexstyl', 'OddsChecker, Something', jasmine.any(Number), jasmine.any(Number))],
          [new UserStats('florianmski', 'Creators, Another', jasmine.any(Number), jasmine.any(Number))]
        );

        expect(actual).toEqual(expected);
      });
  }));

  it('does not remove "Verified" and "Scheduled" from project names if they are in valid positions', async(() => {
    givenStats({
      'usersStats': {
        'alexstyl': {
          'assignedProjectsStats': {': Verified YOLO :': 128, ': Scheduled DAWG :': 128},
          'assignedProjectsContributions': 256
        }
      }
    });

    client.getCompanyStats(ANY_DATE, ANY_DATE, ANY_TIMEZONE)
      .subscribe((actual: CompanyStats) => {
        let expected: CompanyStats = new CompanyStats(
          [],
          [new UserStats('alexstyl', ': Verified YOLO :, : Scheduled DAWG :', jasmine.any(Number), jasmine.any(Number))]
        );

        expect(actual).toEqual(expected);
      });
  }));

  it('removes duplicates after removing "Verified" and "Scheduled" from project names when concatenating', async(() => {
    givenStats({
      'usersStats': {
        'alexstyl': {
          'assignedProjectsStats': {'OddsChecker: Verified': 128, 'OddsChecker: Scheduled': 128},
          'assignedProjectsContributions': 256
        },
        'florianmski': {
          'assignedProjectsStats': {'Creators: Scheduled': 161, 'Creators: Verified': 1},
          'assignedProjectsContributions': 162
        },
      }
    });

    client.getCompanyStats(ANY_DATE, ANY_DATE, ANY_TIMEZONE)
      .subscribe((actual: CompanyStats) => {
        let expected: CompanyStats = new CompanyStats(
          [],
          [
            new UserStats('alexstyl', 'OddsChecker', jasmine.any(Number), jasmine.any(Number)),
            new UserStats('florianmski', 'Creators', jasmine.any(Number), jasmine.any(Number))
          ]
        );

        expect(actual).toEqual(expected);
      });
  }));

});
