import {combineReducers, createFeatureSelector} from '@ngrx/store';

export const reducer = combineReducers<FoldersFeatureSlice>({});

const selectFoldersFeatureSlice = createFeatureSelector<FoldersFeatureSlice>('folders-route');

