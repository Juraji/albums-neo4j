import installArrayExt from './array-ext';
import installObjExt from './object-ext';

export const installExtensions = () => {
  installArrayExt();
  installObjExt();
};
