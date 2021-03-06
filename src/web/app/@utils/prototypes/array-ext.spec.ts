import arrayExtensions from './array-ext';

arrayExtensions();

describe('array-ext', () => {

  describe('chunks', () => {
    it('should chunk an array', () => {
      const arr = [1, 2, 3, 4, 5, 6, 7, 8, 9];
      const result = arr.chunks(4);

      expect(result).toEqual([[1, 2, 3, 4], [5, 6, 7, 8], [9]]);
    });
  });

  describe('transpose2d', () => {
    it('should throw error on 1d array', () => {
      const arr = [1, 2, 3];

      expect(() => arr.transpose2d()).toThrowError('Invalid array length');
    });

    it('should transpose a regular matrix', () => {
      const arr = [[1, 2, 3], [4, 5, 6], [7, 8, 9]];
      const result = arr.transpose2d();

      expect(result).toEqual([[1, 4, 7], [2, 5, 8], [3, 6, 9]]);
    });

    it('should transpose an irregular matrix', () => {
      const arr = [[1, 2, 3, 4], [5, 6, 7, 8, 9]];
      const result = arr.transpose2d();

      expect(result).toEqual([[1, 5], [2, 6], [3, 7], [4, 8], [9]]);
    });
  });
});
