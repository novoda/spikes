import { Component } from '@angular/core';
import { WidgetEvent } from './../WidgetEvent';
import { DashboardComponent } from './../DashboardComponent';

@Component({
  selector: 'app-external-url',
  templateUrl: 'external-url.component.html',
  styleUrls: ['external-url.component.scss']
})
export class ExternalUrlComponent implements DashboardComponent {

  private url: string;

  public update(event: WidgetEvent) {
    this.url = event.payload;
  }

}
