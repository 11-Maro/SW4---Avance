package com.icesi.mio.parser;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para CSVReader
 */
class CSVReaderTest {

    @Test
    void testReadCSV() throws IOException {
        // Crear archivo CSV temporal
        Path tempFile = Files.createTempFile("test", ".csv");
        Files.writeString(tempFile, """
                COL1,COL2,COL3
                value1,value2,value3
                value4,value5,value6
                """);

        try {
            List<String[]> rows = CSVReader.readCSV(tempFile.toString());
            
            assertEquals(2, rows.size());
            assertEquals("value1", rows.get(0)[0]);
            assertEquals("value2", rows.get(0)[1]);
            assertEquals("value3", rows.get(0)[2]);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void testReadHeader() throws IOException {
        Path tempFile = Files.createTempFile("test", ".csv");
        Files.writeString(tempFile, """
                HEADER1,HEADER2,HEADER3
                value1,value2,value3
                """);

        try {
            String[] header = CSVReader.readHeader(tempFile.toString());
            
            assertEquals(3, header.length);
            assertEquals("HEADER1", header[0]);
            assertEquals("HEADER2", header[1]);
            assertEquals("HEADER3", header[2]);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
}
