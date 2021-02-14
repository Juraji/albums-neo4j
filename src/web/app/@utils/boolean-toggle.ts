import {BehaviorSubject} from "rxjs";

export class BooleanToggle extends BehaviorSubject<boolean> {

  constructor(state = false) {
    super(state);
  }

  toggle() {
    this.next(!this.value)
  }
}
