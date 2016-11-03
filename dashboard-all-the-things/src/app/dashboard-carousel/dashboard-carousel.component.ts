import { ExternalUrlComponent } from '../dashboards/external-url/external-url.component';
import { ReviewComponent } from '../dashboards/review';
import { SonarCoverageComponent } from '../dashboards/sonar-coverage';
import { StackOverflowComponent } from '../dashboards/stackoverflow/stackoverflow.component';
import { WidgetEvent } from '../dashboards/WidgetEvent';
import { DynamicComponent } from './dynamic.component';
import { SocketService } from './socket.service';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ConnectableObservable, Subscription } from 'rxjs';

let COMPONENTS = [
  { key: "coverage", type: SonarCoverageComponent },
  { key: "ciWall", type: ExternalUrlComponent },
  { key: "reviews", type: ReviewComponent },
  { key: "stackoverflow", type: StackOverflowComponent }
];

@Component({
  selector: 'app-dashboard-carousel',
  templateUrl: 'dashboard-carousel.component.html',
  styleUrls: ['dashboard-carousel.component.scss'],
  entryComponents: COMPONENTS.map((el) => el.type)
    .filter((elem, index, arr) => arr.indexOf(elem) === index)
})
export class DashboardCarouselComponent implements OnInit, OnDestroy {

  socketService: SocketService;
  connection: Subscription;

  type: any;
  event: WidgetEvent;

  @ViewChild(DynamicComponent) dynamicComponent: DynamicComponent;

  constructor(socketService: SocketService) {
    this.socketService = socketService;
  }

  ngOnInit(): void {
    let observable: ConnectableObservable<any> = this.socketService.create('http://localhost:3002');
    this.connection = observable
      .subscribe((event: WidgetEvent) => {
        if (event.widgetKey === undefined) {
          return;
        }
        let res = this.findComponentFor(event.widgetKey);
        if (res != undefined) {
          this.type = res.type;
          this.event = event;
        }
      });
    observable.connect();
  }

  findComponentFor(key: string) {
    return COMPONENTS.filter((elem, index, arr) => elem.key === key)[0];
  }

  ngOnDestroy(): void {
    if (!this.connection.closed) {
      this.connection.unsubscribe();
    }
  }

}
