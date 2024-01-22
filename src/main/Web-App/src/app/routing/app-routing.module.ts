import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {HomeComponent} from "../home.component/home.component";
import { RepositoryComponent } from '../repository.component/repository.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'repository/:id', component: RepositoryComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
