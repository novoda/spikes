import { BiggestSlackerComponent } from "../dashboards/slack/biggest-slacker/biggest-slacker.component";
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
import { GalleryComponent } from '../dashboards/slack/gallery/gallery.component'
import { ThanksComponent } from '../dashboards/slack/thanks/thanks.component'
import { MostActiveChannelComponent } from '../dashboards/slack/most-active-channel/most-active-channel.component'

const COMPONENTS = {
  coverage: SonarCoverageComponent,
  ciWall: ExternalUrlComponent,
  reviews: ReviewComponent,
  stackoverflow: StackOverflowComponent,
  biggestSlacker: BiggestSlackerComponent,
  gallery: GalleryComponent,
  thanks: ThanksComponent,
  mostActiveChannel: MostActiveChannelComponent
};

const distinct = (elem, index, arr) => arr.indexOf(elem) === index;

@Component({
  selector: 'app-dashboard-carousel',
  templateUrl: 'dashboard-carousel.component.html',
  styleUrls: ['dashboard-carousel.component.scss'],
  entryComponents: Object['values'](COMPONENTS).filter(distinct)
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
    const type = COMPONENTS[event.widgetKey];
    if (type != undefined) {
      this.type = type;
      this.event = event;
    }
  };

  ngOnDestroy(): void {
    if (!this.configSubscription.closed) {
      this.configSubscription.unsubscribe();
    }
    if (!this.connection.closed) {
      this.connection.unsubscribe();
    }
  }

}
