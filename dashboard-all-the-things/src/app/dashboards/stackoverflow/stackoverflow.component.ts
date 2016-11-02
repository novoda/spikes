import { Component, OnInit, Input } from '@angular/core';
import { Question } from './Question';
import { WidgetEvent } from './../WidgetEvent';
import { DashboardComponent } from './../DashboardComponent';

@Component({
  selector: 'app-stackoverflow',
  templateUrl: 'stackoverflow.component.html',
  styleUrls: ['stackoverflow.component.scss']
})
export class StackOverflowComponent implements DashboardComponent {

  private questions: Array<Question>;
  private questionToShow: Question;

  public update(event: WidgetEvent) {
    this.questions = event.payload.questions;
    this.questionToShow = this.pickRandomQuestion();
  }

  private pickRandomQuestion(): Question {
    let question = this.questions[Math.floor(Math.random() * (this.questions.length))];
    return <Question> question;
  }

}
