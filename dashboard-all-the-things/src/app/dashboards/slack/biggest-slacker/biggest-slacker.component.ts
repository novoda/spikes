import { SlackUser } from './../SlackUser';
import { Component, Input } from '@angular/core';
import { WidgetEvent } from './../../WidgetEvent';
import { DashboardComponent } from './../../DashboardComponent';

@Component({
  selector: 'app-slack-biggest-slacker',
  templateUrl: 'biggest-slacker.component.html',
  styleUrls: ['biggest-slacker.component.scss', '../../dashboards-common.scss']
})
export class BiggestSlackerComponent implements DashboardComponent {

  private user: SlackUser;

  public update(event: WidgetEvent) {
    this.user = new SlackUser(event.payload.user);
  }

}
