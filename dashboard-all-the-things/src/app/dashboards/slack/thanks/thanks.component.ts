import { SlackUser } from './../SlackUser';
import { Component, Input } from '@angular/core';
import { WidgetEvent } from './../../WidgetEvent';
import { DashboardComponent } from './../../DashboardComponent';

@Component({
  selector: 'app-slack-thanks',
  templateUrl: 'thanks.component.html',
  styleUrls: ['thanks.component.scss', '../../dashboards-common.scss']
})
export class ThanksComponent implements DashboardComponent {

  private user: SlackUser;
  private message: string;

  public update(event: WidgetEvent) {
    this.user = new SlackUser(event.payload.user);
    this.message = event.payload.thanks.text;
  }

}
