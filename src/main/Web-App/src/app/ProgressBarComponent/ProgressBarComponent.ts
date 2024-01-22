import { Component , Input } from '@angular/core';
import { WebSocketService } from '../service/websocket.service';

/**
 * This component is used to display the progress bar
 * @Author: Tagnan Tremellat
 */
@Component({
  selector: 'app-progress-bar',
  templateUrl: './ProgressBarComponent.html',
  styleUrls: ['./ProgressBarComponent.css'],
})
export class ProgressBarComponent {
  @Input() isDarkMode = false
  @Input() progress = 0;
  @Input() isLoading: boolean = false;

  constructor(private webSocketService: WebSocketService) {
  }
  ngOnInit() {  this.webSocketService.progressUpdates.subscribe   (progress => {
    this.progress = progress;
  });
  }
}
