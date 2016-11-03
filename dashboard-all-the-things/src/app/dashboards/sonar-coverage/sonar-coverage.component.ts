import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { SonarService } from './sonar.service';
import { Subscription, Observable } from 'rxjs';
import { OnErrorIgnoreOperator } from './../../shared/OnErrorIgnoreOperator';

@Component({
  selector: 'app-sonar-coverage',
  templateUrl: 'sonar-coverage.component.html',
  styleUrls: ['sonar-coverage.component.scss']
})
export class SonarCoverageComponent implements OnInit, OnDestroy {

  static REFRESH_RATE_IN_MILLISECONDS = 5 * 1000;

  @Input() sonarCoverage: string; // TODO: type to Coverage data

  private sonarService: SonarService;
  private subscription: Subscription;

  constructor(sonarService: SonarService) {
    this.sonarService = sonarService;
  }

  ngOnInit(): void {
    this.subscription = Observable
      .timer(0, SonarCoverageComponent.REFRESH_RATE_IN_MILLISECONDS)
      .switchMap(() => {
        return this.getSonarCoverage();
      })
      .subscribe((coverage: string) => {
        this.sonarCoverage = coverage;
      });
  }

  private getSonarCoverage(): Observable<string> { // TODO: type to Coverage data
    return this.sonarService
      .getProjects()
      .lift(new OnErrorIgnoreOperator<string>()); // TODO: type to Coverage data
  }

  ngOnDestroy(): void {
    if (!this.subscription.isUnsubscribed) {
      this.subscription.unsubscribe();
    }
  }

}

// -------------------------------

// function coverage() {
//   return httpClient(PROJECTS_REQUEST)
//     .then(parseProjects)
//     .then(toAllCoverage)
//     .then(toRuleResult);
// }

// function parseProjects(body) {
//   var jsonBody = JSON.parse(body);
//   var results = jsonBody.map(each => {
//     return {
//       name: each.nm,
//       key: each.k
//     }
//   });
//   return Promise.resolve(results);
// }

// function toAllCoverage(data) {
//   var allCoverage = data.map(each => {
//     var coverageRequest = createCoverageRequest(each.key);
//     return httpClient(coverageRequest)
//       .then(parseCoverage(each.name));
//   });
//   return Promise.all(allCoverage);
// }

// function createCoverageRequest(key) {
//   return {
//     url: 'https://sonar.novoda.com/api/measures/component',
//     qs: {
//       componentKey: key,
//       metricKeys: 'coverage'
//     },
//     auth: {
//       user: sonarAuth,
//       pass: '',
//     }
//   };
// }

// function parseCoverage(projectName) {
//   return function (body) {
//     var jsonBody = JSON.parse(body);
//     var measures = jsonBody.component.measures;
//     if (measures && measures.length > 0) {
//       return Promise.resolve({
//         project: projectName,
//         coverage: measures[0].value
//       });
//     } else {
//       return Promise.resolve({});
//     }
//   }
// }

// function toRuleResult(data) {
//   var filtered = data.filter(each => {
//     return each.project;
//   });

//   incrementIndex(filtered.length);

//   return Promise.resolve({
//     widgetKey: 'coverage',
//     payload: filtered[currentIndex]
//   });
// }

// function incrementIndex(dataLength) {
//   if (currentIndex >= dataLength - 1) {
//     currentIndex = 0;
//   } else {
//     currentIndex++;
//   }
// }
