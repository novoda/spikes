import { Review } from './Review';
import { Component, Input } from '@angular/core';
import { WidgetEvent } from './../WidgetEvent';
import { DashboardComponent } from './../DashboardComponent';

@Component({
  selector: 'app-review',
  templateUrl: 'review.component.html',
  styleUrls: ['review.component.scss', '../dashboards-common.scss']
})
export class ReviewComponent implements DashboardComponent {

  private review: Review;

  public update(event: WidgetEvent) {
    const reviews = event.payload.motivators;
    const selection = this.randomItem(reviews);
    this.review = new Review(event.payload.appName, selection.score, selection.text);
  }

  private randomItem(array: Array<any>) {
    return array[Math.floor(Math.random() * array.length)];
  }

}
