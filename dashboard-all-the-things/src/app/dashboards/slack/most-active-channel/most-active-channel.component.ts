import { SlackUser } from './../SlackUser';
import { Component, Input } from '@angular/core';
import { WidgetEvent } from './../../WidgetEvent';
import { DashboardComponent } from './../../DashboardComponent';

@Component({
  selector: 'app-slack-most-active-channel',
  templateUrl: 'most-active-channel.component.html',
  styleUrls: ['most-active-channel.component.scss', '../../dashboards-common.scss']
})
export class MostActiveChannelComponent implements DashboardComponent {

  private name: string;

  public update(event: WidgetEvent) {
    this.name = event.payload.channel.name;
  }

}
