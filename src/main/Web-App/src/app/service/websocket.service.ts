import { Injectable } from '@angular/core';
import { WebSocketSubject } from 'rxjs/webSocket';

@Injectable({
  providedIn: 'root'
})
/**
 * This service is used to get the progress updates
 * @Author: Tagnan Tremellat
 */
export class WebSocketService {
  private socket$: WebSocketSubject<any>;

  constructor() {
    this.socket$ = new WebSocketSubject<any>('ws://localhost:8080/progress');
  }

  /**
   * This method is used to get the progress updates
   */
  get progressUpdates() {
    return this.socket$.asObservable();
  }
}
