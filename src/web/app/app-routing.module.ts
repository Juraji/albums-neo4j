import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

const routes: Routes = [
  { path: 'directories', loadChildren: () => import('./routes/+directories/directories.module').then(m => m.DirectoriesModule) },
  { path: 'pictures', loadChildren: () => import('./routes/+pictures/pictures.module').then(m => m.PicturesModule) },
  {path: '**', redirectTo: 'directories'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
