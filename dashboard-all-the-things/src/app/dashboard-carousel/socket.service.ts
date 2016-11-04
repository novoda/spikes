import { Observer, Observable, ConnectableObservable } from 'rxjs';
import { Injectable } from '@angular/core';
import * as io from 'socket.io-client';

@Injectable()
export class SocketService {

  private observable: ConnectableObservable<MessageEvent>;

  public create(url): ConnectableObservable<MessageEvent> {
    if (!this.observable) {
      this.observable = this.createObservable(url);
    }
    return this.observable;
  }

  private createObservable(url): ConnectableObservable<any> {
    const socket = io.connect(url, {
      'reconnection': true,
      'reconnectionDelay': 3000,
      'reconnectionAttempts': 100
    });

    const observable: Observable<any> = Observable.create((observer: Observer<any>) => {
      // The Observable will not complete or fail since we want to reconnect if possible.
      socket.on('message', observer.next.bind(observer));
    });
    return observable.publish();
  }

}