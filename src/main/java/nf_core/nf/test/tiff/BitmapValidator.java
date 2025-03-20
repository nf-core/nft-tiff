package nf_core.nf.test.tiff;

import mil.nga.tiff.FileDirectory;
import mil.nga.tiff.Rasters;
import mil.nga.tiff.TIFFImage;

import java.util.List;
import java.util.Objects;

/**
 * Ignores metadata and compares raw bitmap data within each dir.
 */
public class BitmapValidator {
  private final TIFFImage image;

  BitmapValidator(TIFFImage image) {
    this.image = image;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BitmapValidator that = (BitmapValidator) o;

    // a tiff file contains multiple 'directories', each containing multiple bitmaps.
    // since the metadata isn't always standard, we can compare information about the
    // raw bitmaps instead
    List<FileDirectory> dirs = image.getFileDirectories();
    List<FileDirectory> thatDirs = that.image.getFileDirectories();

    if (dirs.size() != thatDirs.size()) {
      throw new RuntimeException(String.format("Number of dirs does not match: '%s' vs '%s'", dirs.size(), thatDirs.size()));
    }
    // NOTE: this assumes dirs are always in same order
    for (int i = 0; i < dirs.size(); i++) {
      if (!matches(dirs.get(i), thatDirs.get(i))) return false;
    }
    return true;
  }

  private boolean matches(FileDirectory dir1, FileDirectory dir2) {
    Rasters r1 = dir1.readRasters();
    Rasters r2 = dir2.readRasters();

    compare(r1.getHeight(), r2.getHeight(), "height");
    compare(r1.getWidth(), r2.getWidth(), "width");
    compare(r1.getBitsPerSample(), r2.getBitsPerSample(), "bitspersample");
    compare(r1.getSamplesPerPixel(), r2.getSamplesPerPixel(), "samplesperpixel");

    int width = r1.getWidth();
    int height = r1.getHeight();
    int bands = r1.getSamplesPerPixel();

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        for (int band = 0; band < bands; band++) {
          Number value1 = r1.getPixelSample(band, x, y);
          Number value2 = r2.getPixelSample(band, x, y);
          compare(value1, value2, "pixel");
        }
      }
    }


    return true;
  }

  private void compare(Object a, Object b, String description) {
    if (!Objects.equals(a, b)) {
      throw new RuntimeException(String.format("Bitmap '%s' does not match: '%s' != '%s'", description, a, b));
    }
  }
}
