import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class SqlToPojoGenerator {
    
    private static final Map<String, String> SQL_TO_JAVA_TYPES = new HashMap<>();

    static {
        SQL_TO_JAVA_TYPES.put("INT", "int");
        SQL_TO_JAVA_TYPES.put("INTEGER", "int");
        SQL_TO_JAVA_TYPES.put("BIGINT", "long");
        SQL_TO_JAVA_TYPES.put("SMALLINT", "short");
        SQL_TO_JAVA_TYPES.put("TINYINT", "byte");
        SQL_TO_JAVA_TYPES.put("FLOAT", "float");
        SQL_TO_JAVA_TYPES.put("DOUBLE", "double");
        SQL_TO_JAVA_TYPES.put("DECIMAL", "java.math.BigDecimal");
        SQL_TO_JAVA_TYPES.put("NUMERIC", "java.math.BigDecimal");
        SQL_TO_JAVA_TYPES.put("CHAR", "String");
        SQL_TO_JAVA_TYPES.put("VARCHAR", "String");
        SQL_TO_JAVA_TYPES.put("TEXT", "String");
        SQL_TO_JAVA_TYPES.put("DATE", "java.sql.Date");
        SQL_TO_JAVA_TYPES.put("TIME", "java.sql.Time");
        SQL_TO_JAVA_TYPES.put("TIMESTAMP", "java.sql.Timestamp");
        SQL_TO_JAVA_TYPES.put("BOOLEAN", "boolean");
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java SqlToPojoGenerator <path_to_ddl_file>");
            return;
        }

        String ddlFilePath = args[0];
        String ddlContent = new String(Files.readAllBytes(Paths.get(ddlFilePath)));
        generatePojos(ddlContent);
    }

    private static void generatePojos(String ddlContent) throws IOException {
        List<Table> tables = parseTables(ddlContent);

        for (Table table : tables) {
            generatePojoFile(table);
        }
    }

    private static List<Table> parseTables(String ddlContent) {
        List<Table> tables = new ArrayList<>();
        Pattern tablePattern = Pattern.compile("CREATE TABLE (\\w+) \\((.+?)\\);", Pattern.DOTALL);
        Matcher tableMatcher = tablePattern.matcher(ddlContent);

        while (tableMatcher.find()) {
            String tableName = tableMatcher.group(1);
            String columnsPart = tableMatcher.group(2);
            List<Column> columns = parseColumns(columnsPart);

            tables.add(new Table(tableName, columns));
        }

        return tables;
    }

    private static List<Column> parseColumns(String columnsPart) {
        List<Column> columns = new ArrayList<>();
        Pattern columnPattern = Pattern.compile("(\\w+) (\\w+)");
        Matcher columnMatcher = columnPattern.matcher(columnsPart);

        while (columnMatcher.find()) {
            String columnName = columnMatcher.group(1);
            String columnType = columnMatcher.group(2);
            String javaType = SQL_TO_JAVA_TYPES.getOrDefault(columnType.toUpperCase(), "String");

            columns.add(new Column(columnName, javaType));
        }

        return columns;
    }

    private static void generatePojoFile(Table table) throws IOException {
        String className = toCamelCase(table.getName(), true);
        File file = new File(className + ".java");
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println("public class " + className + " {");

            for (Column column : table.getColumns()) {
                writer.println("    private " + column.getType() + " " + column.getName() + ";");
            }

            writer.println();
            generateGettersAndSetters(writer, table.getColumns());

            writer.println("}");
        }
    }

    private static void generateGettersAndSetters(PrintWriter writer, List<Column> columns) {
        for (Column column : columns) {
            String camelCaseName = toCamelCase(column.getName(), false);
            String capitalizedCamelCaseName = toCamelCase(column.getName(), true);

            writer.println("    public " + column.getType() + " get" + capitalizedCamelCaseName + "() {");
            writer.println("        return " + column.getName() + ";");
            writer.println("    }");

            writer.println();
            writer.println("    public void set" + capitalizedCamelCaseName + "(" + column.getType() + " " + column.getName() + ") {");
            writer.println("        this." + column.getName() + " = " + column.getName() + ";");
            writer.println("    }");

            writer.println();
        }
    }

    private static String toCamelCase(String text, boolean capitalizeFirst) {
        String[] parts = text.split("_");
        StringBuilder camelCase = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].toLowerCase();

            if (i > 0 || capitalizeFirst) {
                part = part.substring(0, 1).toUpperCase() + part.substring(1);
            }

            camelCase.append(part);
        }

        return camelCase.toString();
    }

    static class Table {
        private final String name;
        private final List<Column> columns;

        public Table(String name, List<Column> columns) {
            this.name = name;
            this.columns = columns;
        }

        public String getName() {
            return name;
        }

        public List<Column> getColumns() {
            return columns;
        }
    }

    static class Column {
        private final String name;
        private final String type;

        public Column(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }
    }
}
