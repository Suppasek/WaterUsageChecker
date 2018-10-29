package com.kmitl.vpower.waterusagechecker;

import android.util.Log;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvFileWriter {

    private static int repeated = 1;

    //Delimiter used in CSV file
    private static final String COMMA_DELIMITER = ",";

    private static final String NEW_LINE_SEPARATOR = "\n";

    //CSV file header
    private static final String FILE_HEADER = "houseNo,Units,unitPrice,amount";

    public static void writeCsvFile(String YearMonth, List<WaterRecord> waterRecords) {

        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(YearMonth + "_water_usage_report");

            //Write the CSV file header
            fileWriter.append(FILE_HEADER);

            //Add a new line separator after the header
            fileWriter.append(NEW_LINE_SEPARATOR);

            //Write a new student object list to the CSV file
            for (WaterRecord record : waterRecords) {
                fileWriter.append(String.valueOf(record.getHouseNo()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(record.getRecordUnit()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(record.getPrice()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(record.getRecordUnit()*record.getPrice()));
                fileWriter.append(NEW_LINE_SEPARATOR);
            }
            Log.d("CSV","CSV file was created successfully !!!");
        } catch (Exception e) {
            Log.d("CSV", "Error in CsvFileWriter !!!");
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }
        }
    }
}

