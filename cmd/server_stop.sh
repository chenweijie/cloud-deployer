#!/bin/bash

basepath=$(cd `dirname $0`; pwd);
dir=$basepath/webapps/
tomcat='-Dcatalina.home=_root_apache-tomcat-6.0.39';
ps -ef|grep 'java'|sed 's#/#_#g'|awk '/'$tomcat'/{print $2}'|xargs kill -9;

echo "stop ok" > text.txt
