package nf_core.nf.test.tiff;

import mil.nga.tiff.TIFFImage;

public class TiffValidator {
  private final TIFFImage image;

  public TiffValidator(TIFFImage image) {
    this.image = image;
  }

  public MetadataValidator getMeta() {
    return new MetadataValidator(image);
  }

  public BitmapValidator getBitmaps() {
    return new BitmapValidator(image);
  }
}
