#!/bin/sh

echo "#ifndef __AUTO_VERSION__"
echo '#define __AUTO_VERSION__ ("`date +%Y%m%d%H%M%S`")'
echo "#endif"
