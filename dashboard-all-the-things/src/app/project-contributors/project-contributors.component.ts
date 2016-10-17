import { Component, Input } from '@angular/core';
import { UserStats } from '../reports/user-stats';

@Component({
  selector: 'app-project-contributors',
  templateUrl: 'project-contributors.component.html',
  styleUrls: ['project-contributors.component.scss']
})
export class ProjectContributorsComponent {

  @Input() projectContributors: Array<UserStats>;

  constructor() {
  }

}
