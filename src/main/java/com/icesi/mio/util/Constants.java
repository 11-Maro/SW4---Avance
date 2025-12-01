package com.icesi.mio.util;

/**
 * Constantes de la aplicación
 */
public class Constants {
    // Rutas de archivos por defecto (ajustadas a los datos reales del proyecto)
    // Directorio donde se encuentran lines-241.csv, stops-241.csv, linestops-241.csv y datagrams*.csv
    public static final String DEFAULT_DATA_DIR = "proyecto-mio/MIO/";
    public static final String LINES_FILE = "lines-241.csv";
    public static final String STOPS_FILE = "stops-241.csv";
    public static final String LINESTOPS_FILE = "linestops-241.csv";
    
    // Orientaciones
    public static final int ORIENTATION_IDA = 0;
    public static final int ORIENTATION_VUELTA = 1;
    
    // Output
    public static final String OUTPUT_DIR = "output/";
    public static final String GRAPHS_DIR = OUTPUT_DIR + "graphs/";
    
    private Constants() {
        // Prevenir instanciación
    }
}
