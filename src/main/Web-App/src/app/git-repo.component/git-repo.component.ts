import {Component, EventEmitter, Input, Output} from '@angular/core';
/**
 * Component for the git-repo form its form to submit a git repository link
 * @author Tagnan Tremellat
 * @version 1.0
 */
@Component({
  selector: 'app-git-repo',
  templateUrl: './git-repo.component.html',
  styleUrls: ['./git-repo.component.css']
})
export class GitRepoComponent {
  link: string = '';
  @Input() isLoading: boolean = false;


  @Output() onRepoSubmit = new EventEmitter<string>();

  submitGitLink(): void {
    this.onRepoSubmit.emit(this.link);
  }
}
