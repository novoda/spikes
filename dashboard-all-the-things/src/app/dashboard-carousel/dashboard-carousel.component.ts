import { ExternalUrlComponent } from '../dashboards/external-url/external-url.component';
import { ReviewComponent } from '../dashboards/review';
import { SonarCoverageComponent } from '../dashboards/sonar-coverage';
import { StackOverflowComponent } from '../dashboards/stackoverflow/stackoverflow.component';
import { WidgetEvent } from '../dashboards/WidgetEvent';
import { DynamicComponent } from './dynamic.component';
import { SocketService } from './socket.service';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ConnectableObservable, Subscription } from 'rxjs';

class DashboardComponents {
  constructor(private foo: any) { }

  toList(): any[] {
    let result: any[] = [];
    for (let key in this.foo) {
      if (result[key] != undefined) {
        continue;
      } 
      let value = this.foo[key];
      result.push(value);
    }
    return result;
  }

  get(key: string): any {
    return this.foo[key];
  }
}

let COMPONENTS = new DashboardComponents({
  "coverage": SonarCoverageComponent,
  "ciWall": ExternalUrlComponent,
  "reviews": ReviewComponent,
  "stackoverflow": StackOverflowComponent
});

@Component({
  selector: 'app-dashboard-carousel',
  templateUrl: 'dashboard-carousel.component.html',
  styleUrls: ['dashboard-carousel.component.scss'],
  entryComponents: COMPONENTS.toList()
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
        this.type = COMPONENTS.get(event.widgetKey) || undefined;
        this.event = event;
      });
    observable.connect();
  }

  ngOnDestroy(): void {
    if (!this.connection.closed) {
      this.connection.unsubscribe();
    }
  }

}
