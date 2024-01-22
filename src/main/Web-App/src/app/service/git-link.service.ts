import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import { Observable } from 'rxjs';
import {Tag} from "../model/repository.model";


@Injectable({
  providedIn: 'root'
})
/**
 * This service is used to get the git link
 */
export class GitLinkService {
  private baseUrl = 'http://localhost:8080';


  constructor(private http: HttpClient) {}

  /**
   * This method is used to get the get repository link by the link
   * @param link
   */
  getGitLink(link: string | null): Observable<any> {
    const requestBody = { url: link };
    const header = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    return this.http.post(`${this.baseUrl}/postLink`, requestBody, {headers : header , responseType: 'text'});
  }

  /**
   * This method is used check if the repository is already imported
   * @param link
   * @param importedRepos
   */
  isRepositoryAlreadyImported(link: string, importedRepos: { id : number ,name: string, link: string , tags : Tag[] }[]): boolean {
    return importedRepos.some(repo => repo.link === link);
  }

  /**
   * This method is used to get the repository by the id
   * @param id
   */
  getRepositoryById(id: number): Observable<any> {
    const header = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    return this.http.post(`${this.baseUrl}/postId`, id, { headers: header, responseType: 'text' });
  }

  /**
   * This method is used to get the contribution by the tag
   * @param tag
   */
  getContributionbyTag(tag: Tag): any {
    const header = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    return this.http.post(`${this.baseUrl}/postContrib`, tag, { headers: header  ,  responseType: 'text' });
  }

  /**
   * This method is used to get the repository by the name
   * @param link
   */
  extractRepoNameFromLink(link: string): string {
    const parts = link.split('/');
    return parts[parts.length - (parts[parts.length - 1] === '' ? 2 : 1)];
  }

  /**
   * This method is used to get all the repositories
   */
  getAllRepos(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/allRepository`);
  }

  /**
   * This method is used to delete the repository by link
   * @param link
   */
   deletedRepo(link: string ) : Observable<any> {
    const header = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    return this.http.post(`${this.baseUrl}/deleteRepo`, link, {headers : header , responseType: 'text'});
  }

}
