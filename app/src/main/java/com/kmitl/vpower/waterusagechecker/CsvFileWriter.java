package com.kmitl.vpower.waterusagechecker;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class CsvFileWriter {

    private static int repeated = 1;

    //Delimiter used in CSV file
    private static final String COMMA_DELIMITER = ",";

    private static final String NEW_LINE_SEPARATOR = "\n";

    //CSV file header
    private static final String FILE_HEADER = "houseNo,Units,unitPrice,amount";

    public static void writeCsvFile(String YearMonth, List<WaterRecord> waterRecords, Context context) {
        String filename = YearMonth + "_water_usage_report";

        try {
//            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
//            if (!root.exists()) {
//                root.mkdirs();
//            }
//            File gpxfile = new File(root, filename);
//            FileWriter writer = new FileWriter(gpxfile);
//            writer.append("A\nB");
//            writer.flush();
//            writer.close();
//            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();

//            FileWriter fileWriter = new FileWriter(filename);
//
//            //Write the CSV file header
//            fileWriter.append(FILE_HEADER);
//
//            //Add a new line separator after the header
//            fileWriter.append(NEW_LINE_SEPARATOR);

            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            String test = "Test\nTest";
            fos.write(test.getBytes());
            fos.close();
            Log.d("CSV", "File was saved");
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
//            StringBuilder fileWriter = new StringBuilder();

            //Write a new student object list to the CSV file
//            for (WaterRecord record : waterRecords) {
//                fileWriter.append(String.valueOf(record.getHouseNo()));
//                fileWriter.append(COMMA_DELIMITER);
//                fileWriter.append(String.valueOf(record.getRecordUnit()));
//                fileWriter.append(COMMA_DELIMITER);
//                fileWriter.append(String.valueOf(record.getPrice()));
//                fileWriter.append(COMMA_DELIMITER);
//                fileWriter.append(String.valueOf(record.getRecordUnit()*record.getPrice()));
//                fileWriter.append(NEW_LINE_SEPARATOR);
//            }
//            outputStreamWriter.write(fileWriter.toString());
//            outputStreamWriter.close();

//            fileWriter.flush();
//            fileWriter.close();
            Log.d("CSV","CSV file was created successfully !!!");
        }  catch (FileNotFoundException fnfe) {
            Log.d("CDV", "File Not Found \nMSG = " + fnfe.getMessage());
        } catch (IOException ioe) {
            Log.d("CSV", "IOException \nMSG = " + ioe.getMessage());
        }
    }
}

