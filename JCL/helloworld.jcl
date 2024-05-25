//IRB872BC JOB 28C21BCB,'TSOBDX53 JAY',MSGCLASS=X,CLASS=A,
//             NOTIFY=&SYSUID
//PDSOUT1 OUTPUT JESDS=ALL,CLASS=J,WRITER=PDSWTR,DEST=LOCAL
//PDSOUT2 OUTPUT JESDS=ALL,CLASS=X,DEST=NCFB
//*
//*      SET DSNJOB=C2BC21
//*      SET GLOBALS=GLOBALS
//*      SET SYSIPLEX=&SYSIPLEX
//*
//JOBPARM SYSAFF=ANY
//*
//*-------------------------------------------------------------
//STEP001 EXEC PROC=JVMPRC16,COND=(4,LT)
//* LOG=LVL=4
//* JAVACLS='helloworld.HelloWorld'
//*ARGS='-pCybt=2021-09-24'
//STDENV DD *
export APP_HOME=/u/bcx3/test1
export BC_JAR=/u/bcx3/test1/helloworld.jar
export JAVA_HOME=/usr/lpp/java/J11.0_64
export JAVA_JDBC_PATH=/usr/lpp/db2/D11E/sanclo/jdbc

# LIBPATH for DB2 and JVM libraries
export LIBPATH=$JAVA_JDBC_PATH/lib:\
$JAVA_HOME/bin:$JAVA_HOME/bin/classic:\
$JAVA_HOME/bin/j9vm

# JAVA_HOME/lib/default
export LIBPATH=$JAVA_JDBC_PATH/lib:\
$JAVA_HOME/lib/classic:$JAVA_HOME/bin:/bin

# Customize your CLASSPATH here
# Add entries for DB2 Universal Driver jarfiles to CLASSPATH
export CLASSPATH=$JAVA_JDBC_PATH/classes/db2jcc.jar:\
$JAVA_JDBC_PATH/classes/db2jcc4.jar

# Add entries for BC JAR
export CLASSPATH=$BC_JAR:$CLASSPATH

# Log environment variables for verification
echo "APP_HOME=$APP_HOME"
echo "BC_JAR=$BC_JAR"
echo "JAVA_HOME=$JAVA_HOME"
echo "JAVA_JDBC_PATH=$JAVA_JDBC_PATH"
echo "CLASSPATH=$CLASSPATH"

# Verify contents of BC_JAR
jar tf $BC_JAR | grep "helloworld/HelloWorld.class"

# Set JZOS specific options
# Use this variable to specify encoding for DD STDOUT and STDERR
#export JZOS_OUTPUT_ENCODING=Cp037
export IBM_JAVA_OPTIONS="$IJO"
$IJO="$IJO -Dspring.profiles.active=bcg"
#export IBM_JAVA_OPTIONS="$IJO"
/*
