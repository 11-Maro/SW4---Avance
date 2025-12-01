package com.icesi.mio.parser;

import com.icesi.mio.model.Stop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser para el archivo stops.csv
 */
public class StopParser {
    private static final Logger logger = LoggerFactory.getLogger(StopParser.class);

    public Map<Integer, Stop> parseStops(String filePath) throws IOException {
        logger.info("Parseando paradas desde: {}", filePath);
        
        List<String[]> rows = CSVReader.readCSV(filePath);
        Map<Integer, Stop> stops = new HashMap<>();

        for (String[] row : rows) {
            try {
                int stopId = Integer.parseInt(row[0].trim());
                int planVersionId = Integer.parseInt(row[1].trim());
                String shortName = row[2].trim();
                String longName = row[3].trim();
                long gpsX = Long.parseLong(row[4].trim());
                long gpsY = Long.parseLong(row[5].trim());
                double decimalLong = Double.parseDouble(row[6].trim());
                double decimalLat = Double.parseDouble(row[7].trim());

                Stop stop = new Stop(stopId, planVersionId, shortName, longName, 
                                   gpsX, gpsY, decimalLong, decimalLat);
                stops.put(stopId, stop);
            } catch (Exception e) {
                logger.warn("Error parseando parada: {}", String.join(",", row), e);
            }
        }

        logger.info("Total de paradas parseadas: {}", stops.size());
        return stops;
    }
}
