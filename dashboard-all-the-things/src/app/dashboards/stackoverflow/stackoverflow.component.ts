import { Component, OnInit, Input } from '@angular/core';
import { Question } from './Question';

@Component({
  selector: 'app-stackoverflow',
  templateUrl: 'stackoverflow.component.html',
  styleUrls: ['stackoverflow.component.scss']
})
export class StackoverflowComponent {

  @Input() question: Question;
  @Input() numberOfQuestions: number;

}
