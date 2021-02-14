import {createReducer, on} from '@ngrx/store';
import {loadRootDirectoriesSuccess} from "@actions/directories.actions";


export interface DirectoriesSliceState {
  tree: Directory[]
}

const initialState: DirectoriesSliceState = {
  tree: []
};


export const reducer = createReducer(
  initialState,
  on(loadRootDirectoriesSuccess, (state, {tree}) => state.copy({tree})),
);

