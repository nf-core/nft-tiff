process COPY_TIFF {
    publishDir params.outdir, enabled: true 

    input:
    path(tiff)

    output:
    path("*.tif*"), emit: tiff

    script:
    """
    cp $tiff ${tiff.baseName}.copy.${tiff.extension}
    """
}