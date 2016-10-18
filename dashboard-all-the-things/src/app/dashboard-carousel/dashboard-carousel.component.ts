import { Component, OnInit, OnDestroy, ElementRef } from '@angular/core';
import { Subscription, Observable, ConnectableObservable } from 'rxjs';
import { SocketService } from './socket.service';

@Component({
  selector: 'app-dashboard-carousel',
  templateUrl: 'dashboard-carousel.component.html',
  styleUrls: ['dashboard-carousel.component.scss']
})
export class DashboardCarouselComponent implements OnInit, OnDestroy {

  private CAROUSEL_CURRENT_POSITION = '--carousel-current-position';
  static ROTATE_RATE_IN_MILLISECONDS = 3 * 1000;

  animation: Subscription;
  position: number;
  dasboardCount: number = 3;
  element: ElementRef;
  socketService: SocketService;
  connection: Subscription;

  constructor(element: ElementRef, socketService: SocketService) {
    this.element = element;
    this.socketService = socketService;
    this.position = 1;
  }

  ngOnInit(): void {
    this.animation = Observable
      .timer(DashboardCarouselComponent.ROTATE_RATE_IN_MILLISECONDS, DashboardCarouselComponent.ROTATE_RATE_IN_MILLISECONDS)
      .subscribe(() => {
        this.showNextDashboard();
      });

    let observable: ConnectableObservable<any> = this.socketService.create('http://localhost:3002');
    this.connection = observable
      .subscribe((event: MessageEvent) => {
        console.log("GOT SOMETHING");
        console.log(event);
      });
    observable.connect();
  }

  private showNextDashboard() {
    if (this.dasboardCount > this.position) {
      this.position += 1;
    } else {
      this.position = 1;
    }
    this.setContributorPosition(this.position);
  }

  private setContributorPosition(position: number) {
    this.element.nativeElement.style.setProperty(this.CAROUSEL_CURRENT_POSITION, position);
  }

  ngOnDestroy(): void {
    if (!this.animation.closed) {
      this.animation.unsubscribe();
    }
    if (!this.connection.closed) {
      this.connection.unsubscribe();
    }
  }

}
