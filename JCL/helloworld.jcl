//BPXJAVAJ JOB (ACCT),'RUN JAVA',CLASS=A,MSGCLASS=A,
//         NOTIFY=&SYSUID
//STEP1    EXEC PGM=BPXBATCH,
//         PARM='sh java -jar /path/to/your/HelloWorld.jar'
//STDOUT   DD SYSOUT=*
//STDERR   DD SYSOUT=*
//STDENV   DD *
// JAVA_HOME=/usr/lpp/java/J8.0_64
// PATH=/bin:/usr/lpp/java/J8.0_64/bin
// CLASSPATH=/path/to/your
// *
