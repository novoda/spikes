import { ExternalUrlComponent } from '../dashboards/external-url/external-url.component';
import { ReviewComponent } from '../dashboards/review';
import { SonarCoverageComponent } from '../dashboards/sonar-coverage';
import { StackOverflowComponent } from '../dashboards/stackoverflow/stackoverflow.component';
import { WidgetEvent } from '../dashboards/WidgetEvent';
import { DynamicComponent } from './dynamic.component';
import { SocketService } from './socket.service';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ConnectableObservable, Subscription, Subscriber } from 'rxjs';
import { ConfigService } from '../config.service';

const COMPONENTS = [
  { key: "coverage", type: SonarCoverageComponent },
  { key: "ciWall", type: ExternalUrlComponent },
  { key: "reviews", type: ReviewComponent },
  { key: "stackoverflow", type: StackOverflowComponent }
];

@Component({
  selector: 'app-dashboard-carousel',
  templateUrl: 'dashboard-carousel.component.html',
  styleUrls: ['dashboard-carousel.component.scss'],
  entryComponents: COMPONENTS.map((elem) => elem.type)
    .filter((elem, index, arr) => arr.indexOf(elem) === index)
})
export class DashboardCarouselComponent implements OnInit, OnDestroy {

  private configService: ConfigService;
  private configSubscription: Subscription;
  private socketService: SocketService;
  private connection: Subscription;
  private type: any;
  private event: WidgetEvent;

  @ViewChild(DynamicComponent) dynamicComponent: DynamicComponent;

  constructor(configService: ConfigService, socketService: SocketService) {
    this.configService = configService;
    this.socketService = socketService;
  }

  ngOnInit(): void {
    this.configSubscription = this.configService.getServerUrl()
      .map((serverUrl: string) => {
        return this.socketService.create(serverUrl);
      }).subscribe((observable: ConnectableObservable<any>) => {
        this.connection = observable.subscribe(this.onWidgetEvent);
        observable.connect();
      });
  }

  private onWidgetEvent = (event: WidgetEvent) => {
    if (event.widgetKey === undefined) {
      return;
    }
    const res = this.findComponentFor(event.widgetKey);
    if (res != undefined) {
      this.type = res.type;
      this.event = event;
    }
  };

  private findComponentFor(key: string) {
    return COMPONENTS.filter((elem, index, arr) => elem.key === key)[0];
  }

  ngOnDestroy(): void {
    if (!this.configSubscription.closed) {
      this.configSubscription.unsubscribe();
    }
    if (!this.connection.closed) {
      this.connection.unsubscribe();
    }
  }

}
