import {Component, EventEmitter, Input,  Output} from '@angular/core';
import { Repository } from '../model/repository.model';
import {Router} from "@angular/router";
import { ActivatedRoute } from '@angular/router';
import {GitLinkService} from "../service/git-link.service";
@Component({
  selector: 'app-repo-list',
  templateUrl: './repo-list.component.html',
  styleUrls: ['./repo-list.component.css']
})

/**
 * This component is used to display the repository list
 * @Author: Tagnan Tremellat
 **/

export class RepoListComponent {
  constructor(private router: Router , private gitLinkService: GitLinkService) {}
  @Input() repositories: Repository[] = [];
  @Output() repositoriesChange = new EventEmitter<Repository[]>();
  filteredRepositories: Repository[] = [];
  isTagCardVisible:{[repositoryId: number]: boolean} = {};
  searchTerm: string = "";

  /**
   * This method is used to initialize the component with the repositories
   */
  ngOnInit() {
    this.filteredRepositories = this.repositories;
  }

  /**
   * This method is used to update the repositories when the input changes
   */
  ngOnChanges() {
    this.filterRepositoriesByName();
    this.clearSearchTerm();
  }

  /**
   * This method is used to toggle the visibility of the tag card
   * @param repository it's for see all the tags of the repository
   */
  toggleTagCardVisibility(repository: Repository) {
    this.isTagCardVisible[repository.id] = !this.isTagCardVisible[repository.id];
    console.log(this.isTagCardVisible);
  }

  /**
   * This method is used to delete a repository
   * @param repository it's the repository to delete
   */
  deleteRepository(repository: Repository): void {
    this.repositories = this.repositories.filter(r => r.id !== repository.id);
    this.filteredRepositories = this.filteredRepositories.filter(r => r.id !== repository.id);
    this.gitLinkService.deletedRepo(repository.link).subscribe();
    this.repositoriesChange.emit(this.repositories);
  }

  /**
   * This method is used to filter the repositories by name
   */
  filterRepositoriesByName(): void {
    if (this.searchTerm) {
      this.filteredRepositories = this.repositories.filter(repo =>
        repo.name.toLowerCase().includes(this.searchTerm.toLowerCase())
      );
    } else {
      this.filteredRepositories = this.repositories;
    }
  }

  /**
   * This method is used to filter the repositories by tag count
   */
  filterByTagCount(): void {
    this.repositories.sort((a, b) => {
      const lengthA = a.tags ? a.tags.length : 0;
      const lengthB = b.tags ? b.tags.length : 0;
      return lengthB - lengthA;
    });
  }

  /**
   * This method is used to filter the repositories by contributor count
   */
  filterByContributorCount(): void {
    this.repositories.sort((a, b) => {
      const lengthA = a.contributors ? a.contributors.length : 0;
      const lengthB = b.contributors ? b.contributors.length : 0;
      return lengthB - lengthA;
    });
  }

  /**
   * This method  it's used to go to the repository page
   * @param repository
   */
  goToRepository(repository: Repository): void {
    this.router.navigate(['/repository', repository.id]).then(success => {
    });
  }

  /**
   * This method is used to clear the search term
   */
  clearSearchTerm() {
    this.searchTerm = "";
    this.filteredRepositories = this.repositories;
  }
}
