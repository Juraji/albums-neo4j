import {ChangeDetectionStrategy, Component, Inject} from '@angular/core';
import {FormControl, Validators} from '@angular/forms';
import {Store} from '@ngrx/store';
import {MODAL_DATA, ModalRef} from '@juraji/ng-bootstrap-modals';
import {FormGroup} from '@utils/forms';
import {ColorEvent} from 'ngx-color';
import {Actions} from '@ngrx/effects';

@Component({
  templateUrl: './edit-tag.modal.html',
  styleUrls: ['./edit-tag.modal.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class EditTagModal {

  readonly form = new FormGroup<Tag>({
    id: new FormControl(), // Container for edited id
    label: new FormControl('New tag', [Validators.required]),
    color: new FormControl('#ff6900', [Validators.required]),
    textColor: new FormControl('#ffffff', [Validators.required]),
  });

  readonly isEditTag: boolean = false;

  constructor(
    private readonly store: Store<AppState>,
    private readonly actions$: Actions,
    private readonly modalRef: ModalRef,
    @Inject(MODAL_DATA) editedTag?: Tag
  ) {
    if (!!editedTag) {
      this.isEditTag = true;
      this.form.setValue(editedTag);
    }
  }

  onFormSubmit() {
    this.modalRef.resolve(this.form.value);
  }

  onDismiss() {
    this.modalRef.dismiss();
  }

  onColorChange(property: keyof NewTagDto, event: ColorEvent) {
    this.form.setValue(this.form.value.copy({[property]: event.color.hex}));
  }
}
