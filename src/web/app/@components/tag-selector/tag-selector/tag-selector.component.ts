import {ChangeDetectionStrategy, Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Store} from '@ngrx/store';
import {Observable, of} from 'rxjs';
import {selectAllTags} from '@reducers/tags';
import {sideEffect} from '@utils/rx';
import {createTag, createTagSuccess, loadAllTags} from '@actions/tags.actions';
import {filter, map, shareReplay} from 'rxjs/operators';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {Actions, ofType} from '@ngrx/effects';

@Component({
  selector: 'app-tag-selector',
  templateUrl: './tag-selector.component.html',
  styleUrls: ['./tag-selector.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TagSelectorComponent implements OnInit {

  readonly createTagForm = new FormGroup({
    label: new FormControl('', [Validators.required]),
    color: new FormControl('#00ff00', [Validators.required])
  });

  readonly tags$: Observable<Tag[]> = this.store.select(selectAllTags)
    .pipe(
      sideEffect(
        (t) => of(t.isEmpty()),
        () => this.store.dispatch(loadAllTags())
      ),
      shareReplay(1)
    );

  readonly tagsIsEmpty$ = this.tags$.pipe(map(arr => arr.isEmpty()));

  @Output()
  readonly tagSelected = new EventEmitter<Tag>();

  constructor(
    private readonly store: Store<AppState>,
    private readonly actions$: Actions,
  ) {
  }

  ngOnInit(): void {
  }

  onTagSelected(tag: Tag) {
    this.tagSelected.emit(tag);
  }

  onCreateTagSubmit() {
    const newTag: NewTagDto = this.createTagForm.value;
    this.store.dispatch(createTag(newTag));
    this.actions$
      .pipe(
        ofType(createTagSuccess),
        filter(t => t.label === newTag.label)
      )
      .subscribe(() => this.createTagForm.reset({label: '', color: ''}));
  }
}
