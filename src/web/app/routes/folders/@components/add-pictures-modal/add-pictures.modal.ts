import {Component, HostBinding, Inject} from '@angular/core';
import {MODAL_DATA, ModalRef, Modals} from '@juraji/ng-bootstrap-modals';
import {FolderPicturesService} from '@services/folder-pictures.service';
import {typedFormControl, typedFormGroup} from 'ngx-forms-typed';
import {Validators} from '@angular/forms';
import {map} from 'rxjs/operators';

const ALLOWED_FILE_TYPES = [
  'image/jpeg',
  'image/bmp',
  'image/gif',
  'image/png',
  'image/tiff',
];

interface AddPicturesForm {
  files: File[];
}

@Component({
  templateUrl: './add-pictures.modal.html',
  styleUrls: ['./add-pictures.modal.scss']
})
export class AddPicturesModal {

  @HostBinding('class.dragging')
  public isDragging = false;

  public readonly form = typedFormGroup<AddPicturesForm>({
    files: typedFormControl<File[]>([], Validators.required)
  });

  public readonly selectedFiles$ = this.form.valueChanges.pipe(
    map(form => form.files)
  );

  constructor(
    private readonly modals: Modals,
    private readonly modalRef: ModalRef,
    private readonly folderPicturesService: FolderPicturesService,
    @Inject(MODAL_DATA) public readonly parentFolder: Folder
  ) {
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;
    const files = Array.from(event.dataTransfer?.items || [])
      .map(it => it.getAsFile())
      .filterEmpty();

    this.filterAndUpdateSelection(files);
  }

  onFileBrowse(event: Event) {
    event.preventDefault();
    event.stopPropagation();

    const files = Array.from((event.target as HTMLInputElement).files || []);
    this.filterAndUpdateSelection(files);
  }

  onRemoveFile(atIndex: number) {
    this.form.setValue(this.form.value.copy(({files}) => ({
      files: files.removeAt(atIndex)
    })));
  }

  onSubmit() {
    const files = this.form.value.files;

    this.folderPicturesService.uploadPictures(this.parentFolder.id, files)
      .subscribe(pictures => this.modalRef.resolve(pictures));
  }

  private filterAndUpdateSelection(files: File[]) {
    const allowedFiles = files.filter(it => ALLOWED_FILE_TYPES.includes(it.type));
    this.form.setValue({
      files: this.form.value.files
        .concat(...allowedFiles)
        .unique(f => f.name)
    });
  }
}
