import { Operator, Subscriber } from 'rxjs';

export class OnErrorIgnoreOperator<T> implements Operator<T, T> {

  call(subscriber: Subscriber<T>, source: any): any {
    return source._subscribe(new OnErrorIgnoreSubscriber<T>(subscriber));
  }

}

class OnErrorIgnoreSubscriber<T> extends Subscriber<T> {

  constructor(private subscriber: Subscriber<T>) {
    super(subscriber);
  }

  protected _next(value: T): void {
    super._next(value);
  }

  protected _error(err: any): void {
    // swallow error
  }

}
