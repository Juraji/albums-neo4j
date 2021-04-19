import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

const routes: Routes = [
  {
    path: 'folders',
    loadChildren: () => import('./routes/folders/folders.module').then(m => m.FoldersModule)
  },
  {
    path: 'duplicates',
    loadChildren: () => import('./routes/duplicates/duplicates.module').then(m => m.DuplicatesModule)
  },
  {
    path: 'tags',
    loadChildren: () => import('./routes/tags/tags.module').then(m => m.TagsModule)
  },
  {path: '**', redirectTo: 'folders'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
