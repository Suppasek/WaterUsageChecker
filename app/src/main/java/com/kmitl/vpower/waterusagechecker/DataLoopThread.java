package com.kmitl.vpower.waterusagechecker;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class DataLoopThread extends Thread {
    private int checkData;
    private String recDate;
    private FirebaseFirestore firebaseFirestore;
    private WaterRecordAdapter recordAdapter;
    private List<WaterRecord> waterRecords;

    public DataLoopThread(FirebaseFirestore firebaseFirestore, WaterRecordAdapter recordAdapter, List<WaterRecord> waterRecords, String recDate) {
        this.firebaseFirestore = firebaseFirestore;
        this.recordAdapter = recordAdapter;
        this.waterRecords = waterRecords;
        this.recDate = recDate;
    }

    @Override
    public void run() {
        checkData = 0;
        synchronized (this) {
            for (int i = 1; i < 41; i++ ) {
                Log.d("overall", "Before Loop " + intToStr(i));
                final int hN = i;

                firebaseFirestore
                        .collection("rooms")
                        .document("house_no " + intToStr(i))
                        .collection("water_usage")
                        .document(recDate)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            checkData++;
                            WaterRecord record = new WaterRecord(
                                    Integer.parseInt(task.getResult().get("recordUnit").toString()),
                                    task.getResult().get("year").toString(),
                                    task.getResult().get("month").toString(),
                                    task.getResult().get("houseNo").toString(),
                                    task.getResult().get("signature").toString(),
                                    Integer.parseInt(task.getResult().get("recordNo").toString()),
                                    Integer.parseInt(task.getResult().get("price").toString())
                            );
                            waterRecords.add(record);
                            recordAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("overall", "No Data for House No." + intToStr(hN));
                        }

                    }
                });
            } notify();
        }
    }

    public int getCheckData() {
        return checkData;
    }

    public String intToStr(int number) {
        return Integer.toString(number);
    }
}
