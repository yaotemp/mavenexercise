class IdRange {
    int startId;
    int endId;

    public IdRange(int startId, int endId) {
        this.startId = startId;
        this.endId = endId;
    }
}

List<IdRange> idRanges = new ArrayList<>();


Connection conn = DriverManager.getConnection("jdbc:your_database_url", "username", "password");
Statement stmt = conn.createStatement();
String query = "SELECT id FROM source_table ORDER BY id";
ResultSet rs = stmt.executeQuery(query);

int counter = 0;
int startId = 0;

while (rs.next() && idRanges.size() < numThreads) {
    int currentId = rs.getInt("id");

    if (counter == 0) {
        startId = currentId;  // Start of a new chunk
    }

    counter++;

    if (counter == chunkSize || rs.isLast()) {
        int endId = currentId;  // End of the current chunk
        idRanges.add(new IdRange(startId, endId));

        counter = 0;  // Reset the counter for the next chunk
    }
}

// Close the resources after use
rs.close();
stmt.close();
conn.close();
