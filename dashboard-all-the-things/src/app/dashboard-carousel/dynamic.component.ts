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
  cmpRef: ComponentRef<DashboardComponent>;
  private isViewInitialized: boolean = false;

  constructor(private componentFactoryResolver: ComponentFactoryResolver) { }

  updateComponent() {
    if (this.type === undefined) {
      return;
    }
    if (!this.isViewInitialized) {
      return;
    }
    if (this.cmpRef) {
      this.cmpRef.destroy();
    }

    let factory = this.componentFactoryResolver.resolveComponentFactory(this.type);
    this.cmpRef = this.target.createComponent(factory)
    this.cmpRef.instance.update(this.event);
  }

  ngOnChanges() {
    this.updateComponent();
  }

  ngAfterViewInit() {
    this.isViewInitialized = true;
    this.updateComponent();
  }

  ngOnDestroy() {
    if (this.cmpRef) {
      this.cmpRef.destroy();
    }
  }
}