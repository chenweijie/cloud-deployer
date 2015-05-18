#!/bin/bash

basepath=$(cd `dirname $0`; pwd);
dir=$basepath/webapps/

if [ "$1"!="" ]; then
   echo "transport stage1 " $1 " OK" > text.txt
   nc -l 12345 > ${dir}/${1}.war
fi
echo "transport stage2 " $1 " OK" > text.txt