#!/bin/bash

basepath=$(cd `dirname $0`; pwd);
dir=$basepath/webapps/

exec "${basepath}/bin/startup.sh"

echo "start OK"