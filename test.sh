#!/bin/bash
nf-test test tests/**/*.nf.test --plugins target/nft-tiff-*.jar "${@}"