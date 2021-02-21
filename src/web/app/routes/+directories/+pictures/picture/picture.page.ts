import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Observable, of} from 'rxjs';
import {filter, map, shareReplay, switchMap} from 'rxjs/operators';
import {Store} from '@ngrx/store';
import {selectPictureById} from '@reducers/pictures';
import {sideEffect} from '@utils/rx/side-effect';
import {fetchPicture} from '@actions/pictures.actions';

@Component({
  templateUrl: './picture.page.html',
  styleUrls: ['./picture.page.scss']
})
export class PicturePage implements OnInit {

  private readonly pictureId$: Observable<string> = this.activatedRoute.paramMap.pipe(
    filter((m) => m.has('pictureId')),
    map((m) => m.get('pictureId') as string)
  );

  readonly picture$: Observable<PictureProps | null> = this.pictureId$.pipe(
    switchMap(pictureId => this.store.select(selectPictureById, pictureId)
      .pipe(map((picture) => ({pictureId, picture})))),
    sideEffect(
      ({picture}) => of(!picture),
      ({pictureId}) => this.store.dispatch(fetchPicture({pictureId}))
    ),
    map(({picture}) => picture),
    shareReplay(1)
  );

  constructor(
    private readonly store: Store<AppState>,
    private readonly activatedRoute: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
  }

}
