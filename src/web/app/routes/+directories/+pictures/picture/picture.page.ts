import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs';
import {filter, map, shareReplay, switchMap} from 'rxjs/operators';
import {Store} from '@ngrx/store';
import {selectPictureById} from '@reducers/pictures';
import {not, sideEffect} from '@utils/rx';
import {fetchPicture} from '@actions/pictures.actions';

@Component({
  templateUrl: './picture.page.html',
  styleUrls: ['./picture.page.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PicturePage implements OnInit {

  private readonly pictureId$: Observable<string> = this.activatedRoute.paramMap.pipe(
    filter((m) => m.has('pictureId')),
    map((m) => m.get('pictureId') as string)
  );

  readonly picture$: Observable<PictureProps | null> = this.pictureId$.pipe(
    sideEffect(
      (pictureId) => this.store.select(selectPictureById, pictureId).pipe(not()),
      (pictureId) => this.store.dispatch(fetchPicture({pictureId}))
    ),
    switchMap(pictureId => this.store.select(selectPictureById, pictureId)
      .pipe(map((picture) => ({pictureId, picture})))),
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
