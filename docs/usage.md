# Metadata

## Full metadata comparison

Metadata can be directly compared using:

```groovy
def tiff      = path('image.tiff').tiff
def reference = path('reference.tiff').tiff

assert tiff.meta == reference.meta
```  

This will check every metadata field in every directory of the TIFF files for equality. 

## Compare dimensions

The height and width of TIFF files can be compared using the `meta.getDimensions()` function:

```groovy
def tiff      = path('image.tiff').tiff
def reference = path('reference.tiff').tiff

def dim          = tiff.meta.getDimensions()
def referenceDim = reference.meta.getDimensions()

assert dim == referenceDim
```

## Check for field counts

The number of metadata fields in each directory of a TIFF file can be checked using the `meta.getFieldCounts()` function:

```groovy
def tiff = path('image.tiff').tiff

assert tiff.meta.getFieldCounts()[0] == 10
```

Note that meta.getFieldCounts() returns a list, where each element corresponds to the directory at the same position.

## Check for field names

The names of metadata fields in each directory of a TIFF file can be checked using the `meta.getFieldNames()` function:

```groovy
def tiff      = path('image.tiff').tiff
def reference = path('reference.tiff').tiff

def fields          = tiff.meta.getFieldNames()
def referenceFields = reference.meta.getFieldNames()

assert fields == referenceFields
assert fields[0].contains('Compression')

```

Note that `meta.getFieldNames()` returns a list, where each element corresponds to the directory at the same position.

## Check individual metadata fields
Individual (lists of) metadata fields in each directory of a TIFF file can be compared using the `meta.getFields()` function:

```groovy
def tiff      = path('image.tiff').tiff
def reference = path('reference.tiff').tiff

def fields          = tiff.meta.getFields(['ImageWidth', 'PlanarConfiguration', 'Compression'])
def referenceFields = reference.meta.getFields(['ImageWidth', 'PlanarConfiguration', 'Compression'])

assert fields == referenceFields
```
Note that `meta.getFields()` returns a `List` of `Map`s. 
Each index in the list corresponds to a directory in the TIFF file. 
The maps assign the field name to the corresponding value.

The function `meta.getFields()` also accepts a single field as a `String` and may be called without any parameters to compare every metadata field.

# Bitmaps

## Full bitmap comparison 
Bitmaps can be directly compared using:

```groovy
def tiff      = path('image.tiff').tiff
def reference = path('reference.tiff').tiff

assert tiff.bitmaps == reference.bitmaps
```

## Bitmap comparison with tolerance
A comparison of bitmaps, where a certain percentage of entries must be equal, can be performed using the `bitmaps.matchesWithTolerance()` function:

```groovy
def tiff      = path('image.tiff').tiff
def reference = path('reference.tiff').tiff

assert tiff.bitmaps.matchesWithTolerance(reference.bitmaps, 80)
```

In this example the TIFF files `image.tiff` and `reference.tiff` are compared and 80% of bitmap entries have to match.

## Bitmap comparison with checksums

The values of bitmaps can be compared using `md5` checksums using the `bitmaps.md5()` function:

```groovy
def tiff      = path('image.tiff').tiff
def reference = path('reference.tiff').tiff

def md5Sum          = tiff.bitmaps.md5()
def referenceMd5Sum = reference.bitmaps.md5()

assert md5Sum == referenceMd5Sum
```

This is particularly useful with snapshots:

```groovy
def tiff = path('image.tiff').tiff
def md5  = tiff.bitmaps.md5()

assert snapshot(md5).match()
```