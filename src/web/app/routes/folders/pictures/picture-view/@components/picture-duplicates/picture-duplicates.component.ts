import {Component, Input} from '@angular/core';
import {Store} from '@ngrx/store';
import {selectDuplicatesByPictureId} from '@ngrx/duplicates';
import {ObserveProperty} from '@utils/decorators';
import {ReplaySubject} from 'rxjs';
import {switchMap} from 'rxjs/operators';

@Component({
  selector: 'app-picture-duplicates',
  templateUrl: './picture-duplicates.component.html',
  styleUrls: ['./picture-duplicates.component.scss']
})
export class PictureDuplicatesComponent {

  @Input()
  picture: Picture | null = null;

  @ObserveProperty('picture')
  readonly picture$ = new ReplaySubject<Picture>();

  readonly duplicates$ = this.picture$.pipe(
    switchMap(({id}) => this.store.select(selectDuplicatesByPictureId, {pictureId: id}))
  );

  constructor(
    private readonly store: Store<AppState>
  ) {
  }

}
