import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {BooleanToggle} from '@utils/boolean-toggle';

interface Link {
  label: string;
  url: string;
}

@Component({
  selector: 'app-main-navbar',
  templateUrl: './main-navbar.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MainNavbarComponent implements OnInit {

  readonly opened$ = new BooleanToggle();

  readonly links: Link[] = [
    {label: 'Directories', url: '/folders'},
    {label: 'Duplicates', url: '/duplicates'},
    {label: 'Tags', url: '/tags'},
  ];

  constructor() {
  }

  ngOnInit(): void {
  }
}
