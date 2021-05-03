import {ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {BooleanToggle} from '@utils/boolean-toggle';
import {typedFormControl, typedFormGroup} from 'ngx-forms-typed';
import {Validators} from '@angular/forms';
import {Store} from '@ngrx/store';
import {updatePicture} from '@ngrx/pictures';

@Component({
  selector: 'app-picture-details',
  templateUrl: './picture-details.component.html',
  styleUrls: ['./picture-details.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PictureDetailsComponent implements OnChanges {

  readonly form = typedFormGroup<Pick<Picture, 'name'>>({
    name: typedFormControl('', Validators.required)
  });

  readonly editing$ = new BooleanToggle(false);

  @Input()
  public folder: BindingType<Folder>;

  @Input()
  public picture: BindingType<Picture>;

  constructor(
    private readonly store: Store<AppState>
  ) {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.hasOwnProperty('picture') && !!this.picture) {
      this.onCancel();
      this.form.reset(this.picture);
    }
  }

  onSubmit() {
    if (!!this.picture) {
      const update = this.picture.copy(this.form.value);
      this.store.dispatch(updatePicture(update));

      this.editing$.setTo(false);
    }
  }

  onCancel() {
    this.editing$.setTo(false);
  }
}
