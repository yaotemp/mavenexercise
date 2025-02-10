#!/bin/bash
# 临时切换环境
export JAVA_HOME=/usr/lpp/java/J8.0_31bit
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=/path/to/db2jcc.jar:/path/to/db2jcc_license_cisuz.jar
export LIBPATH=/path/to/db2/native/lib:$LIBPATH

# 编译并运行
javac JDBCTest.java
java JDBCTest
