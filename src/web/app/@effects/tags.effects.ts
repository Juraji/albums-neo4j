import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType, ROOT_EFFECTS_INIT} from '@ngrx/effects';
import {EffectMarker} from '@utils/decorators';
import {map, mapTo, switchMap} from 'rxjs/operators';
import {TagsService} from '@services/tags.service';
import {
  createTag,
  createTagSuccess,
  deleteTag,
  deleteTagSuccess,
  loadTags,
  loadTagsSuccess,
  updateTag,
  updateTagSuccess
} from '@actions/tags.actions';


@Injectable()
export class TagsEffects {

  @EffectMarker
  loadAllTags$ = createEffect(() => this.actions$.pipe(
    ofType(ROOT_EFFECTS_INIT, loadTags),
    switchMap(() => this.tagsService.getAllTags()),
    map(loadTagsSuccess)
  ));

  @EffectMarker
  createTag$ = createEffect(() => this.actions$.pipe(
    ofType(createTag),
    switchMap(({tag}) => this.tagsService.createTag(tag)),
    map(createTagSuccess)
  ));

  @EffectMarker
  updateTag$ = createEffect(() => this.actions$.pipe(
    ofType(updateTag),
    switchMap(({tag}) => this.tagsService.updateTag(tag)),
    map(updateTagSuccess)
  ));

  @EffectMarker
  deleteTag$ = createEffect(() => this.actions$.pipe(
    ofType(deleteTag),
    switchMap(({tag}) => this.tagsService.deleteTag(tag.id).pipe(mapTo(tag))),
    map(deleteTagSuccess)
  ));

  constructor(
    private readonly actions$: Actions,
    private readonly tagsService: TagsService
  ) {
  }
}
