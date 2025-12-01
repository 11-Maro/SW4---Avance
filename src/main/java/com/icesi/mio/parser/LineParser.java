package com.icesi.mio.parser;

import com.icesi.mio.model.Line;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser para el archivo lines.csv
 */
public class LineParser {
    private static final Logger logger = LoggerFactory.getLogger(LineParser.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public Map<Integer, Line> parseLines(String filePath) throws IOException {
        logger.info("Parseando rutas desde: {}", filePath);
        
        List<String[]> rows = CSVReader.readCSV(filePath);
        Map<Integer, Line> lines = new HashMap<>();

        for (String[] row : rows) {
            try {
                // Estructura real del archivo:
                // 0:LINEID, 1:PLANVERSIONID, 2:SHORTNAME, 3:DESCRIPTION, 
                // 4:PLANVERSIONID(duplicado), 5:ACTIVATIONDATE, 6:CREATIONDATE
                int lineId = Integer.parseInt(row[0].trim());
                int planVersionId = Integer.parseInt(row[1].trim());
                String shortName = row[2].trim();
                String description = row[3].trim();
                // Usar columna 5 para ACTIVATIONDATE (la columna 4 es PLANVERSIONID duplicado)
                LocalDateTime activationDate = LocalDateTime.parse(row[5].trim(), DATE_FORMATTER);

                Line line = new Line(lineId, planVersionId, shortName, description, activationDate);
                lines.put(lineId, line);
            } catch (Exception e) {
                logger.warn("Error parseando línea: {}", String.join(",", row), e);
            }
        }

        logger.info("Total de rutas parseadas: {}", lines.size());
        return lines;
    }
}
