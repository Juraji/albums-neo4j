import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {EffectMarker} from '@utils/effect-marker.annotation';
import {
  createTag,
  createTagSuccess,
  deleteTag,
  deleteTagSuccess,
  loadAllTags,
  loadAllTagsSuccess,
  updateTag,
  updateTagSuccess
} from '@actions/tags.actions';
import {map, mapTo, switchMap} from 'rxjs/operators';
import {TagsService} from '@services/tags.service';


@Injectable()
export class TagsEffects {

  @EffectMarker
  loadAllTags$ = createEffect(() => this.actions$.pipe(
    ofType(loadAllTags),
    switchMap(() => this.tagsService.getAllTags()),
    map((tags) => loadAllTagsSuccess(tags))
  ));

  @EffectMarker
  createTag$ = createEffect(() => this.actions$.pipe(
    ofType(createTag),
    switchMap(({newTag}) => this.tagsService.createTag(newTag)),
    map((tag) => createTagSuccess(tag))
  ));

  @EffectMarker
  updateTag$ = createEffect(() => this.actions$.pipe(
    ofType(updateTag),
    switchMap(({tag}) => this.tagsService.updateTag(tag)),
    map((tag) => updateTagSuccess(tag))
  ));

  @EffectMarker
  deleteTag$ = createEffect(() => this.actions$.pipe(
    ofType(deleteTag),
    switchMap(({tag}) => this.tagsService.deleteTag(tag).pipe(mapTo(tag))),
    map((tag) => deleteTagSuccess(tag))
  ));

  constructor(
    private readonly actions$: Actions,
    private readonly tagsService: TagsService
  ) {
  }
}
