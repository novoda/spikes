import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class ReportsMockService {

  constructor() {
  }

  getAggregatedStats(): Observable<{usersStats: any}> {
    return Observable
      .from([{
        'usersStats': {
          'tasomaniac': {
            'assignedProjectsStats': {},
            'assignedProjectsContributions': 0,
            'externalRepositoriesStats': {},
            'externalRepositoriesContributions': 0
          },
          'takecare': {
            'assignedProjectsStats': {'R \u0026 D: Scheduled': 253},
            'assignedProjectsContributions': 253,
            'externalRepositoriesStats': {},
            'externalRepositoriesContributions': 0
          },
          'Mecharyry': {
            'assignedProjectsStats': {'The Times: Scheduled': 252},
            'assignedProjectsContributions': 252,
            'externalRepositoriesStats': {},
            'externalRepositoriesContributions': 0
          },
          'alexstyl': {
            'assignedProjectsStats': {'OddsChecker: Verified': 128, 'OddsChecker: Scheduled': 128},
            'assignedProjectsContributions': 256,
            'externalRepositoriesStats': {'all-4': 8, 'spikes': 15},
            'externalRepositoriesContributions': 23
          },
          'florianmski': {
            'assignedProjectsStats': {'Creators: Scheduled': 161},
            'assignedProjectsContributions': 161,
            'externalRepositoriesStats': {},
            'externalRepositoriesContributions': 0
          },
          'dominicfreeston': {
            'assignedProjectsStats': {'OddsChecker: Scheduled': 167},
            'assignedProjectsContributions': 167,
            'externalRepositoriesStats': {},
            'externalRepositoriesContributions': 0
          },
          'wltrup': {
            'assignedProjectsStats': {'OddsChecker: Verified': 59, 'OddsChecker: Scheduled': 98},
            'assignedProjectsContributions': 157,
            'externalRepositoriesStats': {'oddschecker-ios': 29, 'oddschecker-android': 2},
            'externalRepositoriesContributions': 31
          },
          'rock3r': {
            'assignedProjectsStats': {'All4: Verified': 906},
            'assignedProjectsContributions': 906,
            'externalRepositoriesStats': {
              'github-reports': 5,
              'merlin': 22,
              'download-manager': 2,
              'spikes': 16,
              'novoda': 1
            },
            'externalRepositoriesContributions': 46
          },
          'mr-archano': {
            'assignedProjectsStats': {},
            'assignedProjectsContributions': 0,
            'externalRepositoriesStats': {},
            'externalRepositoriesContributions': 0
          },
          'amlcurran': {
            'assignedProjectsStats': {},
            'assignedProjectsContributions': 0,
            'externalRepositoriesStats': {},
            'externalRepositoriesContributions': 0
          },
          'gbasile': {
            'assignedProjectsStats': {'OddsChecker: Verified': 43},
            'assignedProjectsContributions': 43,
            'externalRepositoriesStats': {},
            'externalRepositoriesContributions': 0
          },
          'blundell': {
            'assignedProjectsStats': {'R \u0026 D: Scheduled': 53},
            'assignedProjectsContributions': 53,
            'externalRepositoriesStats': {},
            'externalRepositoriesContributions': 0
          },
          'yvettecook': {
            'assignedProjectsStats': {'OddsChecker: Verified': 26, 'OddsChecker: Scheduled': 42},
            'assignedProjectsContributions': 68,
            'externalRepositoriesStats': {'spikes': 25, 'oddschecker-ios': 9},
            'externalRepositoriesContributions': 34
          },
          'danybony': {
            'assignedProjectsStats': {'R \u0026 D: Scheduled': 62},
            'assignedProjectsContributions': 62,
            'externalRepositoriesStats': {},
            'externalRepositoriesContributions': 0
          },
          'frapontillo': {
            'assignedProjectsStats': {'R \u0026 D: Scheduled': 352},
            'assignedProjectsContributions': 352,
            'externalRepositoriesStats': {
              'sqlite-provider': 11,
              'github-reports': 85,
              'merlin': 7,
              'spikes': 30,
              'aosp.changelog.to': 1,
              'snowy-village-wallpaper': 2
            },
            'externalRepositoriesContributions': 136
          },
          'qqipp': {
            'assignedProjectsStats': {'OddsChecker: Scheduled': 1},
            'assignedProjectsContributions': 1,
            'externalRepositoriesStats': {},
            'externalRepositoriesContributions': 0
          },
          'tobiasheine': {
            'assignedProjectsStats': {},
            'assignedProjectsContributions': 0,
            'externalRepositoriesStats': {},
            'externalRepositoriesContributions': 0
          },
          'joetimmins': {
            'assignedProjectsStats': {'R \u0026 D: Scheduled': 8},
            'assignedProjectsContributions': 8,
            'externalRepositoriesStats': {},
            'externalRepositoriesContributions': 0
          },
          'ouchadam': {
            'assignedProjectsStats': {'All4: Verified': 472, 'All4: Scheduled': 472},
            'assignedProjectsContributions': 944,
            'externalRepositoriesStats': {
              'sqlite-provider': 2,
              'github-reports': 4,
              'gradle-android-command-plugin': 8,
              'spikes': 38,
              'snowy-village-wallpaper': 12,
              'soundcloud-creators': 2
            },
            'externalRepositoriesContributions': 66
          },
          'fourlastor': {
            'assignedProjectsStats': {'All4: Verified': 657, 'All4: Scheduled': 657},
            'assignedProjectsContributions': 1314,
            'externalRepositoriesStats': {},
            'externalRepositoriesContributions': 0
          },
          'eduardourso': {
            'assignedProjectsStats': {},
            'assignedProjectsContributions': 0,
            'externalRepositoriesStats': {},
            'externalRepositoriesContributions': 0
          },
          'Hutch4': {
            'assignedProjectsStats': {},
            'assignedProjectsContributions': 0,
            'externalRepositoriesStats': {},
            'externalRepositoriesContributions': 0
          },
          'Dorvaryn': {
            'assignedProjectsStats': {'R \u0026 D: Scheduled': 15},
            'assignedProjectsContributions': 15,
            'externalRepositoriesStats': {},
            'externalRepositoriesContributions': 0
          },
          'lgvalle': {
            'assignedProjectsStats': {'The Times: Scheduled': 223},
            'assignedProjectsContributions': 223,
            'externalRepositoriesStats': {'sqlite-provider': 2, 'merlin': 3},
            'externalRepositoriesContributions': 5
          },
          'xrigau': {
            'assignedProjectsStats': {'Creators: Scheduled': 190},
            'assignedProjectsContributions': 190,
            'externalRepositoriesStats': {},
            'externalRepositoriesContributions': 0
          },
          'hhaouat': {
            'assignedProjectsStats': {},
            'assignedProjectsContributions': 0,
            'externalRepositoriesStats': {},
            'externalRepositoriesContributions': 0
          },
          'PaNaVTEC': {
            'assignedProjectsStats': {'All4: Verified': 223, 'All4: Scheduled': 223},
            'assignedProjectsContributions': 446,
            'externalRepositoriesStats': {},
            'externalRepositoriesContributions': 0
          },
          'jackSzm': {
            'assignedProjectsStats': {'All4: Verified': 198, 'All4: Scheduled': 198},
            'assignedProjectsContributions': 396,
            'externalRepositoriesStats': {},
            'externalRepositoriesContributions': 0
          },
          'Electryc': {
            'assignedProjectsStats': {'OddsChecker: Scheduled': 230},
            'assignedProjectsContributions': 230,
            'externalRepositoriesStats': {},
            'externalRepositoriesContributions': 0
          },
          'stefanhoth': {
            'assignedProjectsStats': {},
            'assignedProjectsContributions': 0,
            'externalRepositoriesStats': {
              'all-4': 229,
              'sqlite-provider': 6,
              'project-d-api': 10,
              'spikes': 11,
              'project-d': 89,
              'piriform-ccleaner': 54,
              'oddschecker-ios': 25,
              'oddschecker-android': 62,
              'soundcloud-creators': 19
            },
            'externalRepositoriesContributions': 505
          },
          'eduardb': {
            'assignedProjectsStats': {'The Times: Scheduled': 60, 'R \u0026 D: Scheduled': 11},
            'assignedProjectsContributions': 71,
            'externalRepositoriesStats': {},
            'externalRepositoriesContributions': 0
          },
          'ataulm': {
            'assignedProjectsStats': {'All4: Verified': 419, 'All4: Scheduled': 419},
            'assignedProjectsContributions': 838,
            'externalRepositoriesStats': {},
            'externalRepositoriesContributions': 0
          },
          'JozefCeluch': {
            'assignedProjectsStats': {'OddsChecker: Verified': 64, 'OddsChecker: Scheduled': 64},
            'assignedProjectsContributions': 128,
            'externalRepositoriesStats': {'spikes': 36, 'piriform-ccleaner': 12},
            'externalRepositoriesContributions': 48
          }
        }
      }]);
  }

}
