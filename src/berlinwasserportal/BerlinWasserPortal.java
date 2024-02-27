/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package berlinwasserportal;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * This class retrieves the data from the Berlin Wasserportal and send them to
 * the FROST server installed by IoT Lab.
 * @author Cedric Crettaz
 */
public class BerlinWasserPortal {

    //private static final String CSV_FILE_PATH = "./CSV/5800312_wasserstand_ew_09_10_2023.csv";
    //private static final String FROST_OBSERVATIONS = "https://frost.iotlab.com/sensorthings/v1.1/Datastreams(18)/Observations";
    
    private static final String CSV_FILE_PATH = "./CSV/5800312_wassertemperatur_ew_09_10_2023.csv";
    private static final String FROST_OBSERVATIONS = "https://frost.iotlab.com/sensorthings/v1.1/Datastreams(19)/Observations";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /*
        // URL to retrieve the CSV file:
        // https://wasserportal.berlin.de/station.php?anzeige=d&station=5800312&sreihe=ew&smode=c&thema=ows&sdatum=09.10.2023&senddatum=27.10.2023
        // Ideally, the dates should be set dynamically: for example, the
        // parameter senddatum could be the date of yesterday.
        Reader reader;
        try
        {
            // Read the CSV file
            reader = Files.newBufferedReader(Paths.get(CSV_FILE_PATH), Charset.forName("windows-1250"));
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()                                                                  
                .setDelimiter(';')
                .setHeader()
                .setSkipHeaderRecord(true)
                .build();
            CSVParser csvParser = new CSVParser(reader, csvFormat);
            for (CSVRecord csvRecord : csvParser)
            {
                String column1 = csvRecord.get(0);
                column1 = BerlinWasserPortal.correctTimestamp(column1);
                String column2 = csvRecord.get(1);

                System.out.println("-----");
                System.out.println("Record No: " + csvRecord.getRecordNumber());               
                System.out.println("Col 1: " + column1);
                System.out.println("Col 2: " + column2);
                
                // Prepare the payload to be sent to the STA instance
                String payload = SensorThings.prepareData(column1, Integer.valueOf(column2));
                System.out.println(payload);
                // Send the data to the FROST server
                SensorThings.post(BerlinWasserPortal.FROST_OBSERVATIONS, payload);
                System.out.println("-----");
            }
        }
        catch (IOException ex) {
            Logger.getLogger(BerlinWasserPortal.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
        
        // URL to retrieve the CSV file:
        // https://wasserportal.berlin.de/station.php?anzeige=d&station=5800312&sreihe=ew&smode=c&thema=owt&sdatum=09.10.2023&senddatum=27.10.2023
        // Ideally, the dates should be set dynamically: for example, the
        // parameter senddatum could be the date of yesterday.
        Reader reader;
        try
        {
            // Read the CSV file
            reader = Files.newBufferedReader(Paths.get(CSV_FILE_PATH), Charset.forName("windows-1250"));
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()                                                                  
                .setDelimiter(';')
                .setHeader()
                .setSkipHeaderRecord(true)
                .build();
            CSVParser csvParser = new CSVParser(reader, csvFormat);
            for (CSVRecord csvRecord : csvParser)
            {
                String column1 = csvRecord.get(0);
                column1 = BerlinWasserPortal.correctTimestamp(column1);
                String column2 = csvRecord.get(1);

                System.out.println("-----");
                System.out.println("Record No: " + csvRecord.getRecordNumber());               
                System.out.println("Col 1: " + column1);
                column2 = column2.replace(',', '.');
                System.out.println("Col 2: " + column2);
                
                // Prepare the payload to be sent to the STA instance
                String payload = SensorThings.prepareTemperatureData(column1, Double.valueOf(column2));
                System.out.println(payload);
                // Send the data to the FROST server
                SensorThings.post(BerlinWasserPortal.FROST_OBSERVATIONS, payload);
                System.out.println("-----");
            }
        }
        catch (IOException ex) {
            Logger.getLogger(BerlinWasserPortal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Correct the timestamp from the CSV file.
     * @param timestamp the timestamp read from the CSV file
     * @return the corrected timestamp
     */
    public static String correctTimestamp(String timestamp)
    {
        // From 16.06.2016 17:00 to 2016-06-16T15:00:00Z
        // Move the hour in Zulu time (-2 hours)
        String pattern = "dd.MM.yyyy hh:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
        String isoDatePattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        SimpleDateFormat isoDateFormat = new SimpleDateFormat(isoDatePattern);
        try
        {
            Date date = simpleDateFormat.parse(timestamp);
            String isoDate = isoDateFormat.format(date);
            return isoDate;
        }
        catch (ParseException ex)
        {
            Logger.getLogger(BerlinWasserPortal.class.getName()).log(Level.SEVERE, null, ex);
            return "0";
        }
    }
}
