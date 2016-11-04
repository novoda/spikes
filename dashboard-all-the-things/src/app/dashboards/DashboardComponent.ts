import { WidgetEvent } from './WidgetEvent';

export abstract class DashboardComponent {

  abstract update(event: WidgetEvent): void;

}