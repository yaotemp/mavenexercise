//IRB872BC JOB 28C21BCB,'TSOBDX53 JAY',MSGCLASS=X,CLASS=A,
//             NOTIFY=&SYSUID
//*
//JOBPARM SYSAFF=ANY
//*-------------------------------------------------------------
//STEP001 EXEC PROC=JVMPRC16
//JAVACLS='helloworld.HelloWorld'
//STDENV DD *
export JAVA_HOME=/usr/lpp/java/J11.0_64
export BC_JAR=/u/bcx3/test1/helloworld.jar

# Customize your CLASSPATH here
export CLASSPATH=$BC_JAR

# Log environment variables for verification
echo "JAVA_HOME=$JAVA_HOME"
echo "BC_JAR=$BC_JAR"
echo "CLASSPATH=$CLASSPATH"

# Verify contents of BC_JAR
jar tf $BC_JAR | grep "helloworld/HelloWorld.class"
/*
