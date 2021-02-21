import {createFeatureSelector, createReducer, createSelector, on} from '@ngrx/store';
import {loadRootDirectoriesSuccess} from '@actions/directories.actions';

const initialState: DirectoriesSliceState = {
  tree: [],
  directories: {},
};

export const reducer = createReducer(
  initialState,
  on(loadRootDirectoriesSuccess, (state, {tree}) => {
    const flatten = (children: Directory[]): Directory[] =>
      children.flatMap((dir) => [dir, ...flatten(dir.children)]);

    const directories = flatten(tree).reduce((acc, n) => acc.copy({[n.id]: n}), {});

    return state.copy({tree, directories});
  }),
);

export const selectDirectoriesState = createFeatureSelector<DirectoriesSliceState>('directories');
export const selectDirectoryTree = createSelector(selectDirectoriesState, (s) => s.tree);
export const selectDirectory = (directoryId: string) => createSelector(
  selectDirectoriesState,
  s => s.directories[directoryId]
);
