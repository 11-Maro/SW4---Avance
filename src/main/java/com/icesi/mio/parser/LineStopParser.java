package com.icesi.mio.parser;

import com.icesi.mio.model.LineStop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser para el archivo linestops.csv
 */
public class LineStopParser {
    private static final Logger logger = LoggerFactory.getLogger(LineStopParser.class);

    public List<LineStop> parseLineStops(String filePath) throws IOException {
        logger.info("Parseando paradas por ruta desde: {}", filePath);
        
        List<String[]> rows = CSVReader.readCSV(filePath);
        List<LineStop> lineStops = new ArrayList<>();

        for (String[] row : rows) {
            try {
                int lineStopId = Integer.parseInt(row[0].trim());
                int stopSequence = Integer.parseInt(row[1].trim());
                int orientation = Integer.parseInt(row[2].trim());
                int lineId = Integer.parseInt(row[3].trim());
                int stopId = Integer.parseInt(row[4].trim());
                int planVersionId = Integer.parseInt(row[5].trim());
                String lineVariant = row.length > 6 ? row[6].trim() : "";
                String lineVariantType = row.length > 7 ? row[7].trim() : "";

                LineStop lineStop = new LineStop(lineStopId, stopSequence, orientation, 
                                                lineId, stopId, planVersionId, 
                                                lineVariant, lineVariantType);
                lineStops.add(lineStop);
            } catch (Exception e) {
                logger.warn("Error parseando línea-parada: {}", String.join(",", row), e);
            }
        }

        logger.info("Total de paradas por ruta parseadas: {}", lineStops.size());
        return lineStops;
    }
}
