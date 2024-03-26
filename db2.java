Blob dbBlob = rslt.getBlob(1); // JDBC standard way to get a Blob
int blobLength = (int) dbBlob.length(); // Get BLOB length
byte[] buffer = new byte[4096]; // Define your buffer; size is 4KB here
int bytesRead = 0;
InputStream inputStream = dbBlob.getBinaryStream();

ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
while ((bytesRead = inputStream.read(buffer)) != -1) {
    outputStream.write(buffer, 0, bytesRead);
}

byte[] blobData = outputStream.toByteArray();

// Close streams to release resources
inputStream.close();
outputStream.close();

// Now you can use blobData as needed
