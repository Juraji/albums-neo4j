import {ChangeDetectionStrategy, Component, Inject} from '@angular/core';
import {Validators} from '@angular/forms';
import {Store} from '@ngrx/store';
import {MODAL_DATA, ModalRef} from '@juraji/ng-bootstrap-modals';
import {ColorEvent} from 'ngx-color';
import {Actions} from '@ngrx/effects';
import {typedFormControl, typedFormGroup} from 'ngx-forms-typed';

@Component({
  templateUrl: './edit-tag.modal.html',
  styleUrls: ['./edit-tag.modal.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class EditTagModal {

  readonly form = typedFormGroup<Tag>({
    id: typedFormControl(), // Container for edited id
    label: typedFormControl('New tag', Validators.required),
    color: typedFormControl('#ff6900', Validators.required),
    textColor: typedFormControl('#ffffff', Validators.required),
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

  onColorChange(property: keyof Pick<Tag, 'color' | 'textColor'>, event: ColorEvent) {
    this.form.setValue(this.form.value.copy({[property]: event.color.hex}));
  }
}
