package com.icesi.mio.parser;

import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilidad genérica para leer archivos CSV
 */
public class CSVReader {
    private static final Logger logger = LoggerFactory.getLogger(CSVReader.class);

    /**
     * Lee un archivo CSV y retorna todas las filas (sin el header)
     */
    public static List<String[]> readCSV(String filePath) throws IOException {
        logger.info("Leyendo archivo CSV: {}", filePath);
        
        List<String[]> rows = new ArrayList<>();
        
        try (com.opencsv.CSVReader reader = new CSVReaderBuilder(new FileReader(filePath))
                .withSkipLines(1) // Saltar header
                .build()) {
            
            String[] line;
            while ((line = reader.readNext()) != null) {
                rows.add(line);
            }
        } catch (CsvValidationException e) {
            throw new IOException("Error validando CSV: " + filePath, e);
        }
        
        logger.info("Archivo leído: {} filas", rows.size());
        return rows;
    }

    /**
     * Lee un archivo CSV y retorna el header
     */
    public static String[] readHeader(String filePath) throws IOException {
        try (com.opencsv.CSVReader reader = new com.opencsv.CSVReader(new FileReader(filePath))) {
            return reader.readNext();
        } catch (CsvValidationException e) {
            throw new IOException("Error validando header CSV: " + filePath, e);
        }
    }
}
