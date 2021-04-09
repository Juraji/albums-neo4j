import {createEntityAdapter} from '@ngrx/entity';
import {createReducer} from '@ngrx/store';

const picturesEntityAdapter = createEntityAdapter<Picture>({
  sortComparer: (a, b) => a.name.localeCompare(b.name)
});

export const picturesEntitySelectors = picturesEntityAdapter.getSelectors();

export const picturesEntityReducer = createReducer(
  picturesEntityAdapter.getInitialState(),
);
