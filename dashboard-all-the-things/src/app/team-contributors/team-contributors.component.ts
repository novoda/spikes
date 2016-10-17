import { Component, Input, OnInit, OnDestroy, ElementRef } from '@angular/core';
import { UserStats } from '../reports/user-stats';
import { Subscription, Observable } from 'rxjs';

@Component({
  selector: 'app-team-contributors',
  templateUrl: 'team-contributors.component.html',
  styleUrls: ['team-contributors.component.scss']
})
export class TeamContributorsComponent implements OnInit, OnDestroy {

  static ROTATE_RATE_IN_MILLISECONDS = 3 * 1000;

  private TEAM_CONTRIBUTOR_POSITION = '--team-contributor-position';
  private TEAM_CONTRIBUTOR_QTY = '--team-contributor-qty';

  animation: Subscription;
  position: number;
  element;

  @Input() teamContributors: Array<UserStats>;

  constructor(element: ElementRef) {
    this.element = element;
    this.position = 1;
  }

  ngOnInit(): void {
    this.animation = Observable
      .timer(TeamContributorsComponent.ROTATE_RATE_IN_MILLISECONDS, TeamContributorsComponent.ROTATE_RATE_IN_MILLISECONDS)
      .subscribe(() => {
        this.slideToNextPosition();
      });
  }

  private slideToNextPosition() {
    if (!this.teamContributors) {
      return;
    }
    const visibleContributorQuantity = this.getVisibleContributorQuantity();
    if (this.teamContributors.length - this.position >= visibleContributorQuantity) {
      this.position += 1;
    } else {
      this.position = 1;
    }
    this.setContributorPosition(this.position);
  }

  private getVisibleContributorQuantity(): number {
    return parseInt(window.getComputedStyle(this.element.nativeElement, null).getPropertyValue(this.TEAM_CONTRIBUTOR_QTY), 10);
  }

  private setContributorPosition(position: number) {
    this.element.nativeElement.style.setProperty(this.TEAM_CONTRIBUTOR_POSITION, position);
  }

  ngOnDestroy(): void {
    if (!this.animation.closed) {
      this.animation.unsubscribe();
    }
  }

}
