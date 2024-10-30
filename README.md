# nft-tiff

An nf-test plugin to write assertions on TIFF files

Currently just a stub with the intention that more specific/accurate checks can be added over time.

It adds a `.tiff` function to the `path` calls in nf-test. For example:
```groovy
def tiff = path('filename.tiff').tiff
```

This tiff validator object exposes two functions, allowing comparison to be based on either the tiff 
metadata or the raw bitmap data. For example:
```groovy
def tiff = path(process.out.tiff[0]).tiff
def reference = path('reference.tiff').tiff

// compare the metadata of the two tiffs, ignoring the bitmap data
assert tiff.meta == reference.meta

// or, compare the bitmaps of the two tiffs, ignoring the metadata
assert tiff.bitmaps == reference.bitmaps
```

Since this is just a stub, both approaches (metadata and bitmaps) it compares only the image height and width.

### Possible extensions

- More detailing comparisons of metadata/bitmap data
- Comparing only specific dirs, rather than all of them
- Specifying lists of metadata fields to compare
- Supporting out-of-order dirs?
