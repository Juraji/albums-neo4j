import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
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
    {label: 'Directories', url: '/directories'},
    {label: 'Duplicates', url: '/duplicates'},
    {label: 'Tags', url: '/tags'},
  ];

  constructor(public route: ActivatedRoute) {
  }

  ngOnInit(): void {
  }
}
