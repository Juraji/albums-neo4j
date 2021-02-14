import {createReducer, createSelector, on} from '@ngrx/store';
import {loadRootDirectoriesSuccess} from "@actions/directories.actions";


const initialState: DirectoriesSliceState = {
  tree: []
};


export const reducer = createReducer(
  initialState,
  on(loadRootDirectoriesSuccess, (state, {tree}) => state.copy({tree})),
);

export const selectDirectories = (state: AppState) => state.directories;
export const selectDirectoryTree = createSelector(selectDirectories, (s) => s.tree)

