import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import {addTagToPicture, removeTagFromPicture} from '@actions/pictures.actions';

@Component({
  selector: 'app-picture-tags',
  templateUrl: './picture-tags.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PictureTagsComponent implements OnInit {

  @Input()
  picture: PictureProps | null = null;

  constructor(
    private readonly store: Store<AppState>
  ) {
  }

  ngOnInit(): void {
  }

  onAddTag(tag: Tag) {
    if (!!this.picture) {
      this.store.dispatch(addTagToPicture(this.picture, tag));
    }
  }

  onRemoveTag(tag: Tag) {
    if (!!this.picture) {
      this.store.dispatch(removeTagFromPicture(this.picture, tag));
    }
  }
}
