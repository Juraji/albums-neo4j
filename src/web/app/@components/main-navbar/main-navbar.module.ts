import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MainNavbarComponent } from './main-navbar/main-navbar.component';
import {NgbNavModule} from '@ng-bootstrap/ng-bootstrap';
import {RouterModule} from '@angular/router';



@NgModule({
  imports: [CommonModule, RouterModule, NgbNavModule],
  declarations: [MainNavbarComponent],
  exports: [MainNavbarComponent]
})
export class MainNavbarModule { }
