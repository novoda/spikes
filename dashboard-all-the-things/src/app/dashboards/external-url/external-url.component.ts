import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-external-url',
  templateUrl: 'external-url.component.html',
  styleUrls: ['external-url.component.scss']
})
export class ExternalUrlComponent  {

  @Input() url: string;

}
