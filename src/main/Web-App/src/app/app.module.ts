import {NgModule} from '@angular/core';
import {ProgressBarComponent} from "./ProgressBarComponent/ProgressBarComponent";
import {AppRoutingModule} from './routing/app-routing.module';
import { GitRepoComponent } from './git-repo.component/git-repo.component';
import { BrowserModule } from '@angular/platform-browser';
import {RepositoryComponent} from "./repository.component/repository.component";
import { AppComponent } from './app.component';
import { RepoListComponent } from './repo-list.component/repo-list.component';
import {FormsModule} from "@angular/forms";
import {HttpClientModule} from "@angular/common/http";
import {FeedbackComponent} from "./feedback.component/feedback.component";
import {HomeComponent} from "./home.component/home.component";
import {NgOptimizedImage} from "@angular/common";

@NgModule({
  declarations: [
    AppComponent,
    RepoListComponent,
    RepositoryComponent,
    GitRepoComponent,
    FeedbackComponent,
    HomeComponent,
    ProgressBarComponent
  ],
    imports: [
        BrowserModule,
        FormsModule,
        HttpClientModule,
        AppRoutingModule,
        NgOptimizedImage,
    ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
