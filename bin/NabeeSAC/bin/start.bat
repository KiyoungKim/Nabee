cd ..
java -classpath .;lib/commons-collections-3.2.jar;lib/commons-pool-1.2.jar;lib/commons-dbcp-1.2.1.jar;lib/log4j-1.2.15.jar;lib/bcel-5.2.jar;bin/com.nabsys.sac.jar;lib/hsqldb-2.2.8-jdk5.jar org.hsqldb.util.MainInvoker com.nabsys.process.nabee.NabeeApp config/nabee.xml 9002 "" org.hsqldb.server.Server --database.0 file:data/NABEE --dbname.0 NABEE --port 9002