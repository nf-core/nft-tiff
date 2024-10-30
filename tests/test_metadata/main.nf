process COPY_TIFF {
    input:
    path(tiff)

    output:
    path("*.tiff"), emit: tiff

    script:
    """
    cp $tiff ${tiff.baseName}.copy.${tiff.extension}
    """
}