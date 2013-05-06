#!/bin/sh

rm -rf target_code/
cd ..
./loxigen.py -ljava #&& ( cd target_code/Modules/openflowj/ && ant  )
