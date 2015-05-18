#!/bin/bash

basepath=$(cd `dirname $0`; pwd);
dir=$basepath/webapps/
#OLD_IFS="$IFS"
#IFS=" "
#args=($line)
#IFS="$OLD_IFS"
#cmmd=${args[0]}
#app=${args[1]}
if [ "$1"!="" ]; then
   cd $dir
   rm -rf $dir/$1*
fi
echo "delete " $1 " OK" > text.txt
