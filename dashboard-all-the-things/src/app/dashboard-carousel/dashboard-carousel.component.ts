import { Component, OnInit, OnDestroy, ElementRef } from '@angular/core';
import { Subscription } from 'rxjs';
import { Observable } from 'rxjs/Observable';

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

  constructor(element: ElementRef) {
    this.element = element;
    this.position = 1;
  }

  ngOnInit(): void {
    this.animation = Observable
      .timer(DashboardCarouselComponent.ROTATE_RATE_IN_MILLISECONDS, DashboardCarouselComponent.ROTATE_RATE_IN_MILLISECONDS)
      .subscribe(() => {
        this.showNextDashboard();
      });
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
    if (!this.animation.isUnsubscribed) {
      this.animation.unsubscribe();
    }
  }

}
