import {createReducer, createSelector, on} from '@ngrx/store';
import {loadRootDirectoriesSuccess} from "@actions/directories.actions";


const initialState: DirectoriesSliceState = {
  tree: [],
  directories: {},
};


export const reducer = createReducer(
  initialState,
  on(loadRootDirectoriesSuccess, (state, {tree}) => {
    const flatten = (directories: Directory[]): Directory[] =>
      directories.flatMap((dir) => [dir, ...flatten(dir.children)])
    const directories = flatten(tree).reduce((acc, n) => acc.copy({[n.id]: n}), {})

    return state.copy({tree, directories});
  }),
);

export const selectDirectories = (state: AppState) => state.directories;
export const selectDirectoryTree = createSelector(selectDirectories, (s) => s.tree)
export const selectDirectory = createSelector(
  selectDirectories,
  (s: DirectoriesSliceState, {directoryId}: { directoryId: string }) => s.directories[directoryId]
)
