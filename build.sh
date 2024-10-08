#!/bin/bash

mvn package && \
echo "Built $(readlink -f target/nft-tiff*.jar)"