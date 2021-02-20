import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {EffectMarker} from '@utils/effect-marker.annotation';
import {loadAllTags, loadAllTagsSuccess} from '@actions/tags.actions';
import {map, switchMap} from 'rxjs/operators';
import {TagsService} from '@services/tags.service';


@Injectable()
export class PicturesEffects {

  @EffectMarker
  loadAllTags = createEffect(() => this.actions$.pipe(
    ofType(loadAllTags),
    switchMap(() => this.tagsService.getAllTags()),
    map((tags) => loadAllTagsSuccess({tags}))
  ));

  constructor(
    private readonly actions$: Actions,
    private readonly tagsService: TagsService
  ) {
  }
}
