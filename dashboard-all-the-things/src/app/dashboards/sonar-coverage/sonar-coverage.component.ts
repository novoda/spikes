import { Coverage } from './Coverage';
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

  static REFRESH_RATE_IN_MILLISECONDS = 60 * 1000 * 60; // every hour

  @Input() sonarCoverage: Array<Coverage>;

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
      .subscribe((coverage: Array<Coverage>) => {
        this.sonarCoverage = coverage;
      });
  }

  private getSonarCoverage(): Observable<Array<Coverage>> {
    return this.sonarService
      .getProjects()
      .lift(new OnErrorIgnoreOperator<Array<Coverage>>());
  }

  ngOnDestroy(): void {
    if (!this.subscription.closed) {
      this.subscription.unsubscribe();
    }
  }

}
