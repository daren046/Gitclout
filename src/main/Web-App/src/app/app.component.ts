import { Component } from '@angular/core';
import { GitLinkService } from './service/git-link.service';
import {Tag} from "./model/repository.model";
import {data} from "autoprefixer";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  ngOnInit() {
  }
}
