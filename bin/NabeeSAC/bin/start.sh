#Nabee Daemon start

echo "Starting Nabee SAC."
cd ../

#java home
LANG=ko_KR.eucKR
LC_ALL=ko_KR.eucKR
export LANG LC_ALL
export JAVA_HOME=$JAVA_HOME:/opt/java1.5

# Path
export PATH=.:$PATH:$JAVA_HOME/bin/;

CLASSPATH=$CLASSPATH:bin/com.nabsys.sac.jar;
CLASSPATH=$CLASSPATH:lib/commons-collections-3.2.jar;
CLASSPATH=$CLASSPATH:lib/commons-pool-1.2.jar;
CLASSPATH=$CLASSPATH:lib/commons-dbcp-1.2.1.jar;
CLASSPATH=$CLASSPATH:lib/hsqldb-2.2.8-jdk5.jar;
CLASSPATH=$CLASSPATH:lib/log4j-1.2.15.jar;
CLASSPATH=$CLASSPATH:lib/bcel-5.2.jar;

export CLASSPATH=$CLASSPATH

nohup java -Du=NabeeSAC org.hsqldb.util.MainInvoker com.nabsys.process.nabee.NabeeApp config/nabee.xml 9002 "" org.hsqldb.server.Server --database.0 file:data/NABEE --dbname.0 NABEE --port 9002 $* 1>/dev/null 2>&1 &

echo $!> ./bin/NabeeSAC.pid
cd ./bin
tail -f ../logs/nabee.log
