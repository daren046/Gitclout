import { Component } from '@angular/core';
import { GitLinkService } from '../service/git-link.service';
import {Contributor, Repository, Tag} from "../model/repository.model";
import {ThemeService} from "../service/theme.service";

/**
 *   Home component for the home page of the application
 * @author Tagnan Tremellat
 * @version 1.0
 */
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  title = 'GitClout';
  message: string = '';
  error: boolean = false;
  isLoading: boolean = false;
  importedRepositories: { id : number, name: string, link: string, contributors: Contributor[],tags: Tag[] }[] = [];
  feedbackMessage: string = '';
  isSuccess: boolean = true;
  currentRepo: any;
  repository: any;
  isDarkMode: boolean ;
  showInfoBubble = false;
  progressValue: number = 0;

  constructor(private gitLinkService: GitLinkService , private themeService : ThemeService) {
    this.isDarkMode = localStorage.getItem('theme') === 'dark';
  }

  /**
   * Method called when the component is initialized
   * It calls the getAllRepos method from the gitLinkService to get all the repositories
   */
  ngOnInit(): void {
    this.gitLinkService.getAllRepos().subscribe((data) => {
      this.importedRepositories = data.map((repo: any) => ({
        id: repo.id,
        name: repo.name,
        link: repo.url,
        contributors: repo.contributors,
        tags: repo.tags
      }));
    });
  }

  /**
   * Method called when you change the theme
   */
  onToggleTheme(): void {
    this.themeService.toggleTheme();
    this.isDarkMode = !this.isDarkMode;
  }

  /**
   * Sets a feedback message after a repository is deleted.
   * The message automatically disappears after 5 seconds.
   *
   * @param message The feedback message to display.
   * @param isSuccess Specifies whether the operation was successful (default is true).
   */
  private setFeedbackMessage(message: string, isSuccess: boolean = true): void {
    this.feedbackMessage = message;
    this.isSuccess = isSuccess;
    setTimeout(() => { this.feedbackMessage = ''; }, 5000);
  }

  /**
   * Determines if every element of the first array is present in the second array.
   * @param array1 The first array to be compared.
   * @param array2 The second array against which the first array is compared.
   * @returns Returns true if every element in array1 is found in array2, false otherwise.
   */
  private arrayContainsOther(array1: any[], array2: any[]): boolean {
    return array1.every(element1 =>
      array2.some(element2 => JSON.stringify(element1) === JSON.stringify(element2))
    );
  }

  /**
   * Determines if two arrays are equal by checking if each array contains all elements of the other.
   * @param array1 The first array to be compared.
   * @param array2 The second array to be compared.
   * @returns Returns true if both arrays contain the same elements, false otherwise.
   */
  private arraysAreEqual(array1: any[], array2: any[]): boolean {
    return this.arrayContainsOther(array1, array2) && this.arrayContainsOther(array2, array1);
  }


  /**
   * Updates the contributors and tags of a specified repository if they have changed.
   *
   * @param link The unique link identifier of the repository to update.
   * @param newData The new data object containing the updated contributors and tags.
   */
  private updateRepository(link:string , newData:any){
    const existingRepoIndex = this.importedRepositories.findIndex(repo => repo.link === link);
    const existingRepo = this.importedRepositories[existingRepoIndex];;
    const isTagsUpdated = !this.arraysAreEqual(existingRepo.tags, newData.tags);
    if (!isTagsUpdated) {
      this.setFeedbackMessage('No updates needed.', true);
      this.isLoading = false;
      return;
    } else {
      this.importedRepositories[existingRepoIndex] = {...existingRepo, contributors: newData.contributors, tags: newData.tags};
      this.setFeedbackMessage('Update successful!', true);
    }
    this.isLoading = false;
  }

  private setNotificationMessage(message: string, isError: boolean = false): void {
    this.message = message;
    this.error = isError;
  }

  private isRepositoryAlreadyImported(link: string): boolean {
    return  this.gitLinkService.isRepositoryAlreadyImported(link, this.importedRepositories);
  }

  /**
   * Method called when the user submits a repository link to import
   * @param link the link of the repository to import
   */
  onRepoSubmit(link: string): void {
    const isImported = this.isRepositoryAlreadyImported(link)
    this.isLoading = true;
    this.gitLinkService.getGitLink(link).subscribe(
      (data) => {
        const newData = JSON.parse(data);
        if (isImported) {
          this.updateRepository(link, newData);
        } else {
          this.handleSuccessfulResponse(link, newData);
        }
      },
      () => this.handleErrorResponse()
    );
  }

  /**
   * Handles the response of the server when the import is successful.
   * @param link The link of the repository to import.
   * @param data The data returned by the server.
   * @private
   */
  private handleSuccessfulResponse(link: string, data: any): void {
    this.setNotificationMessage('the Depot exists');
    this.setFeedbackMessage('Import successful!', true);
    this.isLoading = false;
    const repoName = this.extractRepoNameFromLink(link);
    this.currentRepo = data;
    this.importedRepositories.push({contributors: this.currentRepo.contributors, id:this.currentRepo.id ,name: repoName, link: link , tags: this.currentRepo.tags});
    this.progressValue = 0;
  }

  /**
   * Handles the response of the server when an error occurs.
   * @private
   */
  private handleErrorResponse(): void {
    this.setFeedbackMessage('Error during import.', false);
    this.isLoading = false;
  }

  /**
   * Extracts the repository name from a given link.
   * @param link
   * @private
   */
  private extractRepoNameFromLink(link: string): string {
    return this.gitLinkService.extractRepoNameFromLink(link);
  }

  /**
   * Method called when the user clicks on the information button
   */
  toggleInfoBubble() {
    this.showInfoBubble = !this.showInfoBubble;
  }

  /**
   * Method called when a repository is deleted
   * @param updatedRepos the list of repositories after the deletion
   */
  onRepositoriesChanged(updatedRepos: Repository[]) {
    this.importedRepositories = updatedRepos;
    this.setFeedbackMessage('Deletion successful !', true);
  }
}
