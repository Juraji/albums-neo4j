import installArrayExt from './array-ext';
import installObjExt from './object-ext';
import installFileExt from './file-ext';

export const installExtensions = () => {
  installArrayExt();
  installObjExt();
  installFileExt();
};
