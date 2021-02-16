import {Component} from '@angular/core';
import {Store} from "@ngrx/store";
import {ActivatedRoute} from "@angular/router";
import {map, switchMap} from "rxjs/operators";
import {selectDirectory} from "@reducers/directories";
import {Observable} from "rxjs";

@Component({
  templateUrl: './directory.page.html',
})
export class DirectoryPage {

  readonly directoryProps$: Observable<Directory> = this.activatedRoute.paramMap.pipe(
    map((m) => m.get("directoryId") || ""),
    switchMap((directoryId) => this.store.select(selectDirectory, {directoryId}))
  )

  constructor(
    private readonly store: Store<AppState>,
    private readonly activatedRoute: ActivatedRoute
  ) {
  }

}
