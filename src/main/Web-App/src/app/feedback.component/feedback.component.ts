/**
 *  Feedback component
 * @author Tagnan Tremellat
 * @version 1.0
 */
import {Component, Input} from "@angular/core";

@Component({
  selector: 'app-feedback',
  templateUrl: './feedback.component.html',
  styleUrls: ['./feedback.component.css']
})
export class FeedbackComponent {
  @Input() message: string = '';
  @Input() isSuccess: boolean = true;


}
