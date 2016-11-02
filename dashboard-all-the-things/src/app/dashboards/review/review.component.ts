import { Review } from './Review';
import { Component, Input } from '@angular/core';
import { WidgetEvent } from './../WidgetEvent';
import { DashboardComponent } from './../DashboardComponent';

@Component({
  selector: 'app-review',
  templateUrl: 'review.component.html',
  styleUrls: ['review.component.scss']
})
export class ReviewComponent implements DashboardComponent {

  private review: Review;

  public update(event: WidgetEvent) {
    let selection = event.payload.motivators[Math.floor((Math.random() * event.payload.motivators.length))];
    this.review = new Review(event.payload.appName, selection.score, selection.text);
  }

}
