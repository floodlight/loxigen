#!/bin/bash
dir=$(readlink -f $(dirname "$0"))
docker run -it -v $dir:/loxi --rm floodlight/loxi-builder "$@"
