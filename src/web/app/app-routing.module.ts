import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

const routes: Routes = [
  {
    path: 'directories',
    loadChildren: () => import('./routes/+directories/directories.module').then(m => m.DirectoriesModule)
  },
  {
    path: 'tags',
    loadChildren: () => import('./routes/+tags/tags.module').then(m => m.TagsModule)
  },
  {
    path: 'duplicates',
    loadChildren: () => import('./routes/+duplicates/duplicates.module').then(m => m.DuplicatesModule)
  },
  {path: '**', redirectTo: 'directories'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
