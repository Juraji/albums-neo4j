import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {typedFormControl, typedFormGroup} from 'ngx-forms-typed';
import {Validators} from '@angular/forms';
import {Store} from '@ngrx/store';
import {selectAllFolders} from '@ngrx/folders';
import {Modals} from '@juraji/ng-bootstrap-modals';
import {SlideShowRunnerModal} from '../@components/slide-show-runner/slide-show-runner.modal';
import {of} from 'rxjs';
import {map, mergeMap} from 'rxjs/operators';
import {filterWhen, once} from '@utils/rx';
import {loadPicturesByFolderId, selectPicturesByFolderId} from '@ngrx/pictures';

@Component({
  templateUrl: './slideshow.page.html',
  styleUrls: ['./slideshow.page.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SlideshowPage implements OnInit {

  readonly availableFolders$ = this.store.select(selectAllFolders);

  readonly slideshowForm = typedFormGroup<SlideshowRequest>({
    folders: typedFormControl<string[]>([], Validators.required),
    random: typedFormControl<boolean>(true),
    autoPlay: typedFormControl<boolean>(true),
    interval: typedFormControl(10, Validators.min(1))
  });

  constructor(
    private readonly store: Store<AppState>,
    private readonly modals: Modals,
  ) {
  }

  ngOnInit(): void {
  }

  onStartSlideShow() {
    // Preload unloaded folders
    of(this.slideshowForm.value)
      .pipe(
        mergeMap(({folders}) => folders),
        filterWhen(folderId => this.store
          .select(selectPicturesByFolderId, {folderId})
          .pipe(map(x => x.isEmpty()))),
      )
      .subscribe({
        next: folderId => this.store.dispatch(loadPicturesByFolderId(folderId)),
        complete: () => this.modals.open(SlideShowRunnerModal, {
          data: this.slideshowForm.value
        })
      });
  }

  onSelectAllFolders() {
    this.availableFolders$
      .pipe(
        once(),
        map(a => a.map(f => f.id))
      )
      .subscribe(folders => this.slideshowForm.patchValue({folders}));
  }
}
