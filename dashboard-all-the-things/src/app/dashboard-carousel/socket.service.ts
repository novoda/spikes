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
    let socket = io(url);

    let observable: Observable<any> = Observable.create((observer: Observer<any>) => {
      socket.on('connection', (data) => {
        console.log("CLIENT CONNECTED");
      });
      socket.on('event', (event) => {
        observer.next(event);
      });
      socket.on('disconnect', () => {
        observer.complete();
      });
      socket.on('connect_error', (error) => {
        observer.error(error);
      });
      socket.on('reconnect_error', (error) => {
        observer.error(error);
      });
    });

    return observable.publish();
  }

}