import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

const routes: Routes = [
  { path: 'directories', loadChildren: () => import('./routes/+directories/directories.module').then(m => m.DirectoriesModule) },
  {path: '**', redirectTo: "directories"}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
