import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

const routes: Routes = [
  {
    path: 'folders',
    loadChildren: () => import('./routes/folders/folders.module').then(m => m.FoldersModule)
  },
  {path: '**', redirectTo: 'folders'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
