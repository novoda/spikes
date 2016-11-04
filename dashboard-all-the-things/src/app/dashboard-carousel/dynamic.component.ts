import {
  AfterViewInit,
  ChangeDetectorRef,
  Compiler,
  Component,
  ComponentFactory,
  ComponentFactoryResolver,
  ComponentRef,
  Input,
  OnChanges,
  ViewChild,
  ViewContainerRef
} from '@angular/core';
import { DashboardComponent } from './../dashboards/DashboardComponent';
import { WidgetEvent } from './../dashboards/WidgetEvent';
import { OnDestroy } from '@angular/core';

@Component({
  selector: 'app-dynamic',
  template: `<div #target></div>`
})
export class DynamicComponent implements OnChanges, AfterViewInit, OnDestroy {
  @ViewChild('target', { read: ViewContainerRef }) target;
  @Input() type;
  @Input() event: WidgetEvent;
  private componentRef: ComponentRef<DashboardComponent>;
  private isViewInitialized: boolean = false;

  constructor(private componentFactoryResolver: ComponentFactoryResolver) { }

  private updateComponent() {
    if (this.type === undefined || !this.isViewInitialized) {
      return;
    }
    if (this.componentRef != null) {
      this.componentRef.destroy();
    }

    const factory = this.componentFactoryResolver.resolveComponentFactory(this.type);
    this.componentRef = this.target.createComponent(factory);
    this.componentRef.instance.update(this.event);
  }

  ngOnChanges() {
    this.updateComponent();
  }

  ngAfterViewInit() {
    this.isViewInitialized = true;
    this.updateComponent();
  }

  ngOnDestroy() {
    if (this.componentRef) {
      this.componentRef.destroy();
    }
  }
}