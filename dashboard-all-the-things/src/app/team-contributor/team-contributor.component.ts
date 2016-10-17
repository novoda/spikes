import { Component, Input } from '@angular/core';
import { UserStats } from '../reports/user-stats';

@Component({
  selector: 'app-team-contributor',
  templateUrl: 'team-contributor.component.html',
  styleUrls: ['team-contributor.component.scss']
})
export class TeamContributorComponent {

  @Input() user: UserStats;

  constructor() {
  }

}
