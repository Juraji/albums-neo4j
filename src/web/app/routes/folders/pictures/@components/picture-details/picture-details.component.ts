import {ChangeDetectionStrategy, Component, Input} from '@angular/core';

@Component({
  selector: 'app-picture-details',
  templateUrl: './picture-details.component.html',
  styleUrls: ['./picture-details.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PictureDetailsComponent {

  @Input()
  public folder: Folder | null = null;

  @Input()
  public picture: Picture | null = null;
}
