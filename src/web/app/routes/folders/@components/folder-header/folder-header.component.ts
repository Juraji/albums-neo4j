import {ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {BooleanToggle} from '@utils/boolean-toggle';
import {typedFormControl, typedFormGroup} from 'ngx-forms-typed';
import {Validators} from '@angular/forms';
import {Store} from '@ngrx/store';
import {updateFolder} from '@ngrx/folders';

@Component({
  selector: 'app-folder-header',
  templateUrl: './folder-header.component.html',
  styleUrls: ['./folder-header.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FolderHeaderComponent implements OnChanges {

  @Input()
  folder: BindingType<Folder>;

  @Input()
  isRoot: BindingType<boolean>;

  readonly editing$ = new BooleanToggle();
  readonly form = typedFormGroup<Pick<Folder, 'name'>>({
    name: typedFormControl<string>('', Validators.required)
  });

  constructor(
    private readonly store: Store<AppState>
  ) {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.hasOwnProperty('folder') && !!this.folder) {
      this.onCancel();
      this.form.reset(this.folder);
    }
  }

  onSubmit() {
    if (!!this.folder) {
      const update = this.folder.copy(this.form.value);
      this.store.dispatch(updateFolder(update));

      this.editing$.setTo(false);
    }
  }

  onCancel() {
    this.editing$.setTo(false);
  }
}
