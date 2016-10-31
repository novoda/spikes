import { Component, OnInit, OnDestroy, ElementRef } from '@angular/core';
import { Subscription, Observable, ConnectableObservable } from 'rxjs';
import { SocketService } from './socket.service';
import { Coverage } from './../dashboards/sonar-coverage/Coverage';
import { Review } from './../dashboards/review/Review';

@Component({
  selector: 'app-dashboard-carousel',
  templateUrl: 'dashboard-carousel.component.html',
  styleUrls: ['dashboard-carousel.component.scss']
})
export class DashboardCarouselComponent implements OnInit, OnDestroy {

  // private CAROUSEL_CURRENT_POSITION = '--carousel-current-position';
  // static ROTATE_RATE_IN_MILLISECONDS = 3 * 1000;

  // animation: Subscription;
  // position: number;
  // dasboardCount: number = 3;
  // element: ElementRef;
  socketService: SocketService;
  connection: Subscription;
  sonarCoverage: Coverage;
  ciWallUrl: string;
  review: Review;

  constructor(element: ElementRef, socketService: SocketService) {
    // this.element = element;
    this.socketService = socketService;
    // this.position = 1;
  }

  ngOnInit(): void {
    // this.animation = Observable
    //   .timer(DashboardCarouselComponent.ROTATE_RATE_IN_MILLISECONDS, DashboardCarouselComponent.ROTATE_RATE_IN_MILLISECONDS)
    //   .subscribe(() => {
    //     this.showNextDashboard();
    //   });

    let observable: ConnectableObservable<any> = this.socketService.create('http://localhost:3002');
    this.connection = observable
      .subscribe((event: MessageEvent) => {
        if (this.isCoverage(event)) {
          this.sonarCoverage = new Coverage(event['payload']['project'], event['payload']['coverage']);
        } else if (this.isCiWall(event)) {
          this.ciWallUrl = event['payload'];
        }  else if (this.isReview(event)) {
          let selection = event['payload'].motivators[Math.floor((Math.random() * event['payload'].motivators.length))];
          this.review = new Review(event['payload']['appName'], selection.score, selection.text);
        } else {
          console.log("GOT SOMETHING");
          console.log(event);
        }
      });
    observable.connect();
  }

  private isCoverage(event: MessageEvent) {
    return 'widgetKey' in event && event['widgetKey'] === 'coverage';
  }

  private isCiWall(event: MessageEvent) {
    return 'widgetKey' in event && event['widgetKey'] === 'ciWall';
  }

  private isReview(event: MessageEvent) {
    return 'widgetKey' in event && event['widgetKey'] === 'reviews';
  }

  // private showNextDashboard() {
  //   if (this.dasboardCount > this.position) {
  //     this.position += 1;
  //   } else {
  //     this.position = 1;
  //   }
  //   this.setContributorPosition(this.position);
  // }

  // private setContributorPosition(position: number) {
  //   this.element.nativeElement.style.setProperty(this.CAROUSEL_CURRENT_POSITION, position);
  // }

  ngOnDestroy(): void {
    // if (!this.animation.closed) {
    //   this.animation.unsubscribe();
    // }
    if (!this.connection.closed) {
      this.connection.unsubscribe();
    }
  }

}
