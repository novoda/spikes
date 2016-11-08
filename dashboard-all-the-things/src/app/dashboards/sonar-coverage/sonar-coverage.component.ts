import { Coverage } from './Coverage';
import { Component } from '@angular/core';
import { WidgetEvent } from './../WidgetEvent';
import { DashboardComponent } from './../DashboardComponent';

@Component({
  selector: 'app-sonar-coverage',
  templateUrl: 'sonar-coverage.component.html',
  styleUrls: ['sonar-coverage.component.scss', '../dashboards-common.scss']
})
export class SonarCoverageComponent implements DashboardComponent {

  private sonarCoverage: Coverage;

  public update(event: WidgetEvent) {
    this.sonarCoverage = <Coverage> event.payload;
  }

}
