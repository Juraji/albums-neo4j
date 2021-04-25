import {ChangeDetectionStrategy, Component, HostListener, Inject, OnDestroy, OnInit} from '@angular/core';
import {MODAL_DATA, ModalFullScreen, ModalRef} from '@juraji/ng-bootstrap-modals';
import {Store} from '@ngrx/store';
import {BehaviorSubject, combineLatest, interval} from 'rxjs';
import {map, mergeMap, shareReplay} from 'rxjs/operators';
import {selectPicturesByFolderId} from '@ngrx/pictures';
import {slideshowAnimation} from '../slide-show.animations';
import {untilDestroyed} from '@utils/until-destroyed';
import {filterWhen, once} from '@utils/rx';

@ModalFullScreen()
@Component({
  templateUrl: './slide-show-runner.modal.html',
  styleUrls: ['./slide-show-runner.modal.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  animations: [slideshowAnimation]
})
export class SlideShowRunnerModal implements OnInit, OnDestroy {

  readonly pictures$ = combineLatest(this.request.folders.map(folderId => this.store.select(selectPicturesByFolderId, {folderId})))
    .pipe(
      mergeMap(result => result),
      map(result => this.request.random ? result.randomize() : result),
      shareReplay(),
    );

  readonly currentIndex$ = new BehaviorSubject(0);

  readonly currentPicture$ = combineLatest([this.pictures$, this.currentIndex$])
    .pipe(map(([pictures, index]) => pictures[index]));

  readonly playing$ = new BehaviorSubject<boolean>(true);

  constructor(
    private readonly modalRef: ModalRef,
    @Inject(MODAL_DATA) public readonly request: SlideshowRequest,
    private readonly store: Store<AppState>,
  ) {
  }

  ngOnInit(): void {
    if (this.request.autoPlay) {
      interval(this.request.interval * 1e3)
        .pipe(untilDestroyed(this), filterWhen(() => this.playing$))
        .subscribe(() => this.onGoIndexDelta(1));
    }
  }

  ngOnDestroy() {
  }

  @HostListener('window:keyup', ['$event'])
  onWindowKeyPress(e: KeyboardEvent) {
    switch (e.key) {
      case 'ArrowLeft':
        return this.onGoIndexDelta(-1);
      case 'ArrowRight':
        return this.onGoIndexDelta(1);
    }
  }

  onGoIndexDelta(delta: -1 | 1) {
    combineLatest([this.pictures$, this.currentIndex$])
      .pipe(
        once(),
        map(([{length}, currentIndex]) => {
          const projectedIndex = currentIndex + delta;
          return projectedIndex < 0 ? length - 1 : projectedIndex >= length ? 0 : projectedIndex;
        })
      )
      .subscribe(idx => this.currentIndex$.next(idx));
  }

  onPausePlay() {
    this.playing$.next(!this.playing$.value);
  }
}
