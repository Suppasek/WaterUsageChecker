package com.kmitl.vpower.waterusagechecker;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class CsvFileWriter {

    private static int repeated = 0;

    //Delimiter used in CSV file
    private static final String COMMA_DELIMITER = ",";

    private static final String NEW_LINE_SEPARATOR = "\n";

    private File file;

    //CSV file header
    private static final String FILE_HEADER = "houseNo,Units,unitPrice,amount\n";

    public static void writeCsvFile(String YearMonth, List<WaterRecord> waterRecords, FragmentActivity fragmentActivity, Context context) {
        String filename = "";
        if (repeated > 0) {
            filename = "water_usage_report_" + YearMonth + "(" + Integer.toString(repeated) + ").txt";
            repeated++;
        } else {
            filename = "water_usage_report_" + YearMonth + ".csv";
            repeated++;
        }
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
        try {
            FileOutputStream fis = new FileOutputStream(file);
            fis.write(FILE_HEADER.getBytes());
            Log.d("CSV", "Before Loop waterRecords");
            for (WaterRecord record : waterRecords) {
                Log.d("CSV", "House No. = " + record.getHouseNo());
                fis.write(record.getHouseNo().getBytes());
                fis.write(COMMA_DELIMITER.getBytes());
                fis.write(String.valueOf(record.getRecordUnit()).getBytes());
                fis.write(COMMA_DELIMITER.getBytes());
                fis.write(String.valueOf(record.getPrice()).getBytes());
                fis.write(COMMA_DELIMITER.getBytes());
                fis.write(String.valueOf(record.getRecordUnit()*record.getPrice()).getBytes());
                fis.write(NEW_LINE_SEPARATOR.getBytes());
                fis.close();
            }
            fis.close();
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
            downloadManager.addCompletedDownload(file.getName(), file.getName(), true, "text/plain",file.getAbsolutePath(),file.length(),true);
            Log.d("CSV", "After Loop waterRecords");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

