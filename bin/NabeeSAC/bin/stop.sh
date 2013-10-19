#nabeeSAC Daemon stop
#1/bin/sh
ps -ef|grep nabeeSAC | awk '{print $2}' | xargs kill
