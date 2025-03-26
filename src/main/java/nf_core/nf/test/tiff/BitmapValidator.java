package nf_core.nf.test.tiff;

import mil.nga.tiff.FileDirectory;
import mil.nga.tiff.Rasters;
import mil.nga.tiff.TIFFImage;

import java.math.BigInteger;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

  /**
   * Compares this image with a reference image and returns true if at least the specified percentage of pixels match.
   * 
   * @param referenceValidator The validator containing the reference image to compare with
   * @param matchPercentage The minimum percentage of pixels that must match (0.0 to 100.0)
   * @return true if the images match with at least the specified percentage
   * @throws RuntimeException if the images have different structural properties
   * @throws IllegalArgumentException if matchPercentage is not between 0 and 100
   */

  public boolean matchesWithTolerance(BitmapValidator referenceValidator, double matchPercentage) {
    if (matchPercentage < 0.0 || matchPercentage > 100.0) {
        throw new IllegalArgumentException("Match percentage must be between 0.0 and 100.0");
    }
    
    List<FileDirectory> dirs = this.image.getFileDirectories();
    List<FileDirectory> refDirs = referenceValidator.image.getFileDirectories();
    
    if (dirs.size() != refDirs.size()) {
        throw new RuntimeException(String.format("Number of dirs does not match: '%s' vs '%s'", dirs.size(), refDirs.size()));
    }
    
    // NOTE: this assumes dirs are always in same order
    for (int i = 0; i < dirs.size(); i++) {
        double actualPercentage = getMatchPercentage(dirs.get(i), refDirs.get(i));
        if (actualPercentage < matchPercentage) {
            throw new RuntimeException(String.format(
                "Images do not match with required tolerance. Required: %.2f%%, Actual: %.4f%%", 
                matchPercentage, actualPercentage));
        }
    }
    
    return true;
  }

  /**
   * Compares two file directories and checks if at least the specified percentage of pixels match.
   * 
   * @param dir1 The first directory
   * @param dir2 The second directory
   * @param matchPercentage The minimum percentage of pixels that must match
   * @return true if at least the specified percentage of pixels match
   * @throws RuntimeException if directories have different structural properties
   */
  private double getMatchPercentage(FileDirectory dir1, FileDirectory dir2) {
      Rasters r1 = dir1.readRasters();
      Rasters r2 = dir2.readRasters();
      
      // These properties must match exactly
      compare(r1.getHeight(), r2.getHeight(), "height");
      compare(r1.getWidth(), r2.getWidth(), "width");
      compare(r1.getBitsPerSample(), r2.getBitsPerSample(), "bitspersample");
      compare(r1.getSamplesPerPixel(), r2.getSamplesPerPixel(), "samplesperpixel");
      
      int width = r1.getWidth();
      int height = r1.getHeight();
      int bands = r1.getSamplesPerPixel();
      
      long totalPixels = (long) width * height * bands;
      long matchingPixels = 0;
      
      // Compare each pixel
      for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
              for (int band = 0; band < bands; band++) {
                  Number value1 = r1.getPixelSample(band, x, y);
                  Number value2 = r2.getPixelSample(band, x, y);
                  
                  if (Objects.equals(value1, value2)) {
                      matchingPixels++;
                  }
              }
          }
      }
      
      return (double) matchingPixels / totalPixels * 100.0;

  }
  
  /**
   * Computes the md5 checksum over all entries in the bitmap of this image
   *
   * @return The md5 checksum as a String
   * @throws RuntimeException if the java runtime does not provide the md5 algorithm
   */
  public String md5() {
    MessageDigest md5;
    try {
      md5 = MessageDigest.getInstance("md5");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }

    for (FileDirectory dir : image.getFileDirectories()) {
      Rasters rasters = dir.readRasters();
      ByteOrder byteOrder = dir.getReader().getByteOrder();

      int height = rasters.getHeight();
      int bands  = rasters.getSamplesPerPixel();

      for (int y = 0; y < height; y++) {
          for (int band = 0; band < bands; band++) {
            md5.update(rasters.getSampleRow(y, band, byteOrder));
          }
        }
      }

    byte[] digest = md5.digest();
    return String.format("%032X", new BigInteger(1, digest));
  }

}
