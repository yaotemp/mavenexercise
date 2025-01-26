```
mvn install:install-file \
    -Dfile=/path/to/db2jcc4.jar \
    -DgroupId=com.ibm.db2 \
    -DartifactId=db2jcc4 \
    -Dversion=4.0 \
    -Dpackaging=jar

```

```
mvn install:install-file \
    -Dfile=/path/to/db2jcc_license_cisuz.jar \
    -DgroupId=com.ibm.db2 \
    -DartifactId=db2jcc-license-cisuz \
    -Dversion=4.0 \
    -Dpackaging=jar

```

```

<dependencies>
    <!-- DB2 JDBC Driver -->
    <dependency>
        <groupId>com.ibm.db2</groupId>
        <artifactId>db2jcc4</artifactId>
        <version>4.0</version>
        <scope>compile</scope>
    </dependency>
    <!-- DB2 License for z/OS -->
    <dependency>
        <groupId>com.ibm.db2</groupId>
        <artifactId>db2jcc-license-cisuz</artifactId>
        <version>4.0</version>
        <scope>compile</scope>
    </dependency>
</dependencies>

```
