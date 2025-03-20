process COPY_TIFF {
    input:
    path(tiff)

    output:
    path("*.tif*"), emit: tiff

    script:
    """
    cp $tiff ${tiff.baseName}.copy.${tiff.extension}
    """
}