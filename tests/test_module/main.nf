process TEST_MODULE {
    input:
    path(input)

    output:
    path("*.{${input.extension}}"), emit: output

    script:
    """
    cp $input ${input.baseName}.copy.${input.extension}
    """
}