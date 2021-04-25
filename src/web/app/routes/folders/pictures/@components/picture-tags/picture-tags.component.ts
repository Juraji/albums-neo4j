import {ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import {ReplaySubject} from 'rxjs';
import {ObserveProperty} from '@utils/decorators';
import {switchMap} from 'rxjs/operators';
import {addTagToPicture, loadTagsByPictureId, removeTagFromPicture, selectTagsByPictureId} from '@ngrx/tags';
import {untilDestroyed} from '@utils/until-destroyed';

@Component({
  selector: 'app-picture-tags',
  templateUrl: './picture-tags.component.html',
  styleUrls: ['./picture-tags.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PictureTagsComponent implements OnInit, OnDestroy {

  @Input()
  picture: Picture | null = null;

  @ObserveProperty('picture')
  readonly picture$ = new ReplaySubject<Picture>();

  readonly pictureTags$ = this.picture$
    .pipe(switchMap(({id}) => this.store.select(selectTagsByPictureId, {pictureId: id})));

  constructor(
    private readonly store: Store<AppState>,
  ) {
  }

  ngOnInit(): void {
    this.picture$
      .pipe(untilDestroyed(this))
      .subscribe(({id}) => this.store.dispatch(loadTagsByPictureId(id)));
  }

  ngOnDestroy() {
  }

  onAddTag(tag: Tag) {
    if (!!this.picture) {
      this.store.dispatch(addTagToPicture(this.picture.id, tag));
    }
  }

  onRemoveTag(tag: Tag) {
    if (!!this.picture) {
      this.store.dispatch(removeTagFromPicture(this.picture.id, tag.id));
    }
  }
}
