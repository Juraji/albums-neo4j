import {BehaviorSubject} from 'rxjs';

export class BooleanToggle extends BehaviorSubject<boolean> {

  constructor(state = false) {
    super(state);
  }

  toggle(): boolean {
    const next = !this.value;
    this.next(!this.value);
    return next;
  }

  setTo(state: boolean) {
    this.next(state);
  }
}
