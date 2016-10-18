import { Component, Input } from '@angular/core';
import { UserStats } from '../reports/user-stats';

@Component({
  selector: 'app-project-contributor',
  templateUrl: 'project-contributor.component.html',
  styleUrls: ['project-contributor.component.scss']
})
export class ProjectContributorComponent {

  @Input() user: UserStats;

  constructor() {
  }

}
