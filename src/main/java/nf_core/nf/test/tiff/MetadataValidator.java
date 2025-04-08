package nf_core.nf.test.tiff;

import mil.nga.tiff.FieldTagType;
import mil.nga.tiff.FileDirectory;
import mil.nga.tiff.FileDirectoryEntry;
import mil.nga.tiff.TIFFImage;

import java.util.*;

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
    return getFields().equals(that.getFields());
  }

  /**
   * Derive height and width for every directory contained in the TIFF file
   * @return A flat list in the format [height_dir1, width_dir1, height_dir2, width_dir2, ...]
   */
  public List<Number> getDimensions() {
    ArrayList<Number> dims = new ArrayList<>();
    List<FileDirectory> dirs = image.getFileDirectories();
    for (FileDirectory dir : dirs) {
      Collections.addAll(dims, dir.getImageHeight(), dir.getImageWidth());
    }
    return dims;
  }

  /**
   * Derive number of metadata fields for every directory in the TIFF file
   * @return A list where index i represents the number of  metadata fields in the ith directory
   */
  public List<Integer> getFieldCounts() {
    List<Integer> fieldCounts = new ArrayList<>();
    List<FileDirectory> dirs = image.getFileDirectories();
    for (FileDirectory dir : dirs) {
      Collections.addAll(fieldCounts, dir.numEntries());
    }
    return fieldCounts;
  }

  /**
   * Get all metadata fields contained for each directory in the TIFF file
   * @return List of List's where the ith list contains the names of metadata fields of the ith directory
   */
  public List<List<String>> getFieldNames() {
    List<List<String>> fieldNames = new ArrayList<>();
    List<FileDirectory> dirs = image.getFileDirectories();
    for (FileDirectory dir : dirs) {
      List<String> names = new ArrayList<>();
      for (FileDirectoryEntry entry : dir.getEntries()) {
        names.add(entry.getFieldTag().toString());
      }
      fieldNames.add(names);
    }
    return fieldNames;
  }

  /**
   * Return the values for a given list of metadata fields.
   * For every metadata field name given to this function it is checked whether the name is a valid TIFF field.
   * @param fieldNames A list of Strings containing the requested metadata fields
   * @return List where the ith entry contains a field_name -> value mapping for the ith directory.
   */
  public List<Map<String,String>> getFields(List<String> fieldNames) {

    // Validate specified fieldNames and get tag types
    List<FieldTagType> tagTypes = new ArrayList<>();
    for (String fieldName : fieldNames) {
      FieldTagType tag = validateFieldName(fieldName);
      tagTypes.add(tag);
    }

    List<Map<String,String>> fields = new ArrayList<>();
    List<FileDirectory> dirs = image.getFileDirectories();

    for (FileDirectory dir : dirs) {
      Map<String,String> fieldName2value = new HashMap<>();

      for (FieldTagType tag : tagTypes) {
        Set<FileDirectoryEntry> directoryEntries = dir.getEntries();

        // in case we find no matching entry
        fieldName2value.put(tag.toString(), null);

        for (FileDirectoryEntry entry : directoryEntries) {
          if (entry.getFieldTag().getId() == tag.getId()) {
            fieldName2value.put(entry.getFieldTag().toString(), entry.getValues().toString());
          }
        }
      }
      fields.add(fieldName2value);
    }
    return fields;
  }

  /**
   * Return the values for a given metadata field.
   * @param fieldName Name of the metadata field.
   * @return List where the ith entry contains a field_name -> value mapping for the ith directory.
   * @see #getFields(List)
   */
  List<Map<String,String>> getFields(String fieldName) {
    return getFields(Collections.singletonList(fieldName));
  }

  /**
   * Return the values for all metadata fields within the image.
   * @return List where the ith entry contains a field_name -> value mapping for the ith directory.
   * @see #getFields(List)
   */
  List<Map<String,String>> getFields() {
    List<String> fieldNames = new ArrayList<>();
    for (FileDirectory dir : image.getFileDirectories()) {
      for (FileDirectoryEntry entry : dir.getEntries()) {
        fieldNames.add(entry.getFieldTag().toString());
      }
    }
    return getFields(fieldNames);
  }


  /**
   * Checks whether a given String is a valid tiff metadata tag and returns th tag, otherwise throws a {@link RuntimeException}.
   * @param fieldName String containing the name that should be validated.
   * @return {@link FieldTagType} associated with fieldName.
   */
  private FieldTagType validateFieldName(String fieldName) {
    FieldTagType tag;
    try {
      tag = FieldTagType.valueOf(fieldName);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Invalid field tag: " + fieldName);
    }
    return tag;
  }
}
