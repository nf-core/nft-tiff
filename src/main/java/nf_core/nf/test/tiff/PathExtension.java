package nf_core.nf.test.tiff;

import mil.nga.tiff.TIFFImage;
import mil.nga.tiff.TiffReader;

import java.io.IOException;
import java.nio.file.Path;

/*
 * Adds custom extensions to the path() method
 *
 * @author: nf-core
 */
public class PathExtension {

  /**
   * Allows calling {@code path('filename').tiff} in nf-test
   */
  public static TiffValidator getTiff(Path self) throws IOException {
    TIFFImage image = TiffReader.readTiff(self.toFile());
    return new TiffValidator(image);
  }

}