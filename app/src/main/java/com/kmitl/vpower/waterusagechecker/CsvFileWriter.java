package com.kmitl.vpower.waterusagechecker;

import android.content.Context;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class CsvFileWriter {

    private static int repeated = 0;

    //Delimiter used in CSV file
    private static final String COMMA_DELIMITER = ",";

    private static final String NEW_LINE_SEPARATOR = "\n";

    private File file;

    //CSV file header
    private static final String FILE_HEADER = "House No.,Month/Year,Recorded,Units,Unit Price,Amount,Recorded Date,Signature\n";

    public static File writeCsvFile(String YearMonth, List<WaterRecord> waterRecords, FragmentActivity fragmentActivity, Context context) {
        String filename = "";
//        if (repeated > 0) {
//            filename = "water_usage_report_" + YearMonth + "(" + Integer.toString(repeated) + ").txt";
//            repeated++;
//        } else {
//            filename = "water_usage_report_" + YearMonth + ".csv";
//            repeated++;
//        }
        filename = "water_usage_report_" + YearMonth + ".csv";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
        try {
            FileOutputStream fis = new FileOutputStream(file);
            fis.write(FILE_HEADER.getBytes());

            Log.d("CSV", "Before Loop waterRecords");

            for (WaterRecord record : waterRecords) {
                Log.d("CSV", "House No. = " + record.getHouseNo());

                fis.write(record.getHouseNo().getBytes());
                fis.write(COMMA_DELIMITER.getBytes());

                fis.write((record.getMonth() + "/" + record.getYear()).getBytes());
                fis.write(COMMA_DELIMITER.getBytes());
//                Log.d("CSV", "Month/Year = " + record.getMonth() + "/" + record.getYear());

                fis.write(String.valueOf(record.getRecordUnit()).getBytes());
                fis.write(COMMA_DELIMITER.getBytes());
//                Log.d("CSV", "Recorded = " + record.getRecordUnit());

                fis.write(String.valueOf(record.getTotalUnit()).getBytes());
                fis.write(COMMA_DELIMITER.getBytes());
//                Log.d("CSV", "Units = " + record.getPrice());

                String waterRate = "N/A";
                if (record.getTotalUnit() != 0) {
                    waterRate = String.valueOf(record.getPrice() / record.getTotalUnit());
                }
                fis.write(waterRate.getBytes());
                fis.write(COMMA_DELIMITER.getBytes());
//                Log.d("CSV", "Unit Price = " + String.valueOf(record.getPrice() / record.getTotalUnit()));

                fis.write(String.valueOf(record.getPrice()).getBytes());
                fis.write(COMMA_DELIMITER.getBytes());
//                Log.d("CSV", "Amount = " + record.getPrice());

                fis.write(record.getRecordDate().getBytes());
                fis.write(COMMA_DELIMITER.getBytes());
//                Log.d("CSV", "Recorded Date = " + record.getRecordDate());

                fis.write(record.getSignature().getBytes());
                fis.write(NEW_LINE_SEPARATOR.getBytes());
//                Log.d("CSV", "Signature = " + record.getSignature());
            }
            fis.close();

//            DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
//            downloadManager.addCompletedDownload(file.getName(), file.getName(), true, "text/plain",file.getAbsolutePath(),file.length(),true);

            Log.d("CSV", "After Loop waterRecords");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

}

