#!/bin/bash
if [ -t 1 ]; then
    tty_flag="-t"
else
    tty_flag=""
fi
dir=$(python -c 'import os; import sys; print os.path.abspath(sys.argv[1])' $(dirname "$0"))
docker run -i $tty_flag --rm -v $dir:/loxi floodlight/loxi-builder-ubuntu14 "$@"
