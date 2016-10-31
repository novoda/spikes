import { Coverage } from './Coverage';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-sonar-coverage',
  templateUrl: 'sonar-coverage.component.html',
  styleUrls: ['sonar-coverage.component.scss']
})
export class SonarCoverageComponent {

  @Input() sonarCoverage: Coverage;

}
