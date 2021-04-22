import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType, ROOT_EFFECTS_INIT} from '@ngrx/effects';
import {EffectMarker} from '@utils/decorators';
import {map, mapTo, switchMap} from 'rxjs/operators';
import {TagsService} from '@services/tags.service';
import {
  addTagToPicture,
  addTagToPictureSuccess,
  createTag,
  createTagSuccess,
  deleteTag,
  deleteTagSuccess,
  loadTags,
  loadTagsByPictureId,
  loadTagsByPictureIdSuccess,
  loadTagsSuccess, removeTagFromPicture, removeTagFromPictureSuccess,
  updateTag,
  updateTagSuccess
} from './tags.actions';
import {PictureTagsService} from '@services/picture-tags.service';
import {switchMapContinue} from '@utils/rx';


@Injectable()
export class TagsEffects {

  @EffectMarker
  loadAllTags$ = createEffect(() => this.actions$.pipe(
    ofType(ROOT_EFFECTS_INIT, loadTags),
    switchMap(() => this.tagsService.getAllTags()),
    map(loadTagsSuccess)
  ));

  @EffectMarker
  loadPictureTags$ = createEffect(() => this.actions$.pipe(
    ofType(loadTagsByPictureId),
    switchMapContinue(({pictureId}) => this.pictureTagsService.getPictureTags(pictureId)),
    map(([{pictureId}, tags]) => loadTagsByPictureIdSuccess(pictureId, tags))
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

  @EffectMarker
  addTagToPicture$ = createEffect(() => this.actions$.pipe(
    ofType(addTagToPicture),
    switchMapContinue(({pictureId, tag}) => this.pictureTagsService.addTagToPicture(pictureId, tag)),
    map(([{pictureId, tag}]) => addTagToPictureSuccess(pictureId, tag))
  ));

  @EffectMarker
  removeTagToPicture$ = createEffect(() => this.actions$.pipe(
    ofType(removeTagFromPicture),
    switchMapContinue(({pictureId, tagId}) => this.pictureTagsService.removeTagFromPicture(pictureId, tagId)),
    map(([{pictureId, tagId}]) => removeTagFromPictureSuccess(pictureId, tagId))
  ));

  constructor(
    private readonly actions$: Actions,
    private readonly tagsService: TagsService,
    private readonly pictureTagsService: PictureTagsService,
  ) {
  }
}
