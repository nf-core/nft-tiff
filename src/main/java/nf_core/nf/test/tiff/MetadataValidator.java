package nf_core.nf.test.tiff;

import mil.nga.tiff.FileDirectory;
import mil.nga.tiff.TIFFImage;

import java.util.List;
import java.util.Objects;

/**
 * Compare the metadata declared in each dir.
 */
public class MetadataValidator {
  private final TIFFImage image;

  MetadataValidator(TIFFImage image) {
    this.image = image;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MetadataValidator that = (MetadataValidator) o;

    // a tiff file contains multiple 'directories', each containing multiple bitmaps
    // consider two 'metadatas' equal if all their directories' stable metadata matches
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

  /**
   * Compare metadata between two dirs
   * <p>
   * Not all tiffs will use the same metadata, so this might not always work. In which
   * case, consider comparing the bitmaps instead.
   */
  private boolean matches(FileDirectory dir1, FileDirectory dir2) {
    compare(dir1.getImageHeight(), dir2.getImageHeight(), "ImageHeight");
    compare(dir1.getImageWidth(), dir2.getImageWidth(), "ImageWidth");
    return true;
  }

  private void compare(Object a, Object b, String description) {
    if (!Objects.equals(a, b)) {
      throw new RuntimeException(String.format("Metadata '%s' does not match: '%s' != '%s'", description, a, b));
    }
  }
}
