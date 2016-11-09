import { SlackUser } from './../SlackUser';
import { Component, Input } from '@angular/core';
import { WidgetEvent } from './../../WidgetEvent';
import { DashboardComponent } from './../../DashboardComponent';

@Component({
  selector: 'app-slack-gallery',
  templateUrl: 'gallery.component.html',
  styleUrls: ['gallery.component.scss', '../../dashboards-common.scss']
})
export class GalleryComponent implements DashboardComponent {

  private user: SlackUser;
  private imageUrl: string;

  public update(event: WidgetEvent) {
    this.user = new SlackUser(event.payload.user);
    this.imageUrl = event.payload.gallery.url;
  }

}
