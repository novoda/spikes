import { Component, OnInit, Input } from '@angular/core';
import { Question } from './Question';
import { WidgetEvent } from './../WidgetEvent';
import { DashboardComponent } from './../DashboardComponent';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-stackoverflow',
  templateUrl: 'stackoverflow.component.html',
  styleUrls: ['stackoverflow.component.scss', '../dashboards-common.scss']
})
export class StackOverflowComponent implements DashboardComponent {

  private questions: Array<Question>;
  private questionToShow: Question;

  public update(event: WidgetEvent) {
    this.questions = event.payload.questions;
    this.questionToShow = this.pickRandomQuestion();
  }

  private pickRandomQuestion(): Question {
    const question = this.randomItem(this.questions);
    return <Question> question;
  }

  private randomItem(array: Array<any>) {
    return array[Math.floor(Math.random() * array.length)];
  }

}
