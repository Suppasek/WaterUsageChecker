package com.kmitl.vpower.waterusagechecker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TableLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OverallFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private List<WaterRecord> waterRecords;

    public OverallFragment() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.waterRecords = new ArrayList<WaterRecord>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overall, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("overall", "In onActivityCreated");
        getValueDB();
//         bossStyle();
//        showResult();
//        oldStyle();
    }

    private void bossStyle() {
        firebaseFirestore.collection("rooms")
                .document("house_no 1")
                .collection("water_usage")
                .document("2018_2")
                .getParent().getParent().getParent()
                .document("house_no 2")
                .collection("water_usage")
                .document("2018_2").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("overall", task.getResult().toString());
                }
            }
        });
    }

    //Just a stupid Loop
    private void showResult() {
        Log.d("overall", "Before Loop Result");
        for (int i = 0; i < 3; i++) {
            Log.d("overall", "In Loop Result i = " + intToStr(i));
            Log.d("overall", intToStr(waterRecords.size()));
            WaterRecord record = waterRecords.get(i);
            String price = intToStr(record.getPrice() * record.getRecordUnit());
            Log.d("overall",
                    "House No." + record.getHouseNo() + " " +
                            "Units = " + record.getRecordUnit() + " " +
                            "Unit Price = " + record.getPrice() + " " +
                            "Amount = " + price
                    );
        }
    }

    public void getValueDB() {
        Log.d("overall", "In getValueDB");

//        TableLayout recordTable = getView().findViewById(R.id.fragment_overall_list);
        ListView recordTable = getView().findViewById(R.id.fragment_overall_list);

        final WaterRecordAdapter recordAdapter = new WaterRecordAdapter(getActivity(), R.layout.fragment_overall_item, waterRecords);

        recordTable.setAdapter(recordAdapter);
        waterRecords.clear();

        for (int i = 1; i < 4; i++ ) {
            Log.d("overall", "Before Loop " + intToStr(i));
            synchronized (this) {
                try {
                    getRecordFromHouses(intToStr(i), "2018_2");
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.d("overall", "In " + intToStr(i) + " Loop Size of List = " + intToStr(waterRecords.size()));
//            Log.d("overall", "House No." + intToStr(i) + " recordUnit = " + intToStr(waterRecords.get(i-1).getRecordUnit()));
        }

        Log.d("overall", "End getValueDB");
    }

    public void getRecordFromHouses(String houseNo, final String recDate) {
        Log.d("overall", "In getRecordFromHouse\nhouseNo = " + houseNo + " recDate = " + recDate);
//        firebaseFirestore
//                .collection("rooms")
//                .document("house_no " + houseNo)
//                .collection("water_usage")
//                .document(recDate)
//                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    Log.d("overall", task.getResult().get("recordUnit").toString());
//                }
//                else {
//                    Log.d("overall", "Error ngai");
//                }
//            }
//        });
//        Log.d("overall", "End getRecordFromHouse");

        firebaseFirestore
                .collection("rooms")
                .document("house_no " + houseNo)
                .collection("water_usage")
                .document(recDate)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    //Check for data retrieval
//                    Log.d("overall_2",
//                            "Room No." + task.getResult().get("houseNo") +
//                                    " Units = " + task.getResult().get("recordUnit") +
//                                    " Unit Price = " + task.getResult().get("price")
//                            );

                    //First Method for Adding record
//                    waterRecords.add(
//                            new WaterRecord(
//                                    Integer.parseInt(task.getResult().get("recordUnit").toString()),
//                                    task.getResult().get("year").toString(),
//                                    task.getResult().get("month").toString(),
//                                    task.getResult().get("houseNo").toString(),
//                                    task.getResult().get("signature").toString(),
//                                    Integer.parseInt(task.getResult().get("recordNo").toString()),
//                                    Integer.parseInt(task.getResult().get("price").toString())
//
//                            )
//                    );

                    //Secord Method for Adding record [it works]
                    WaterRecord record = new WaterRecord(
                      Integer.parseInt(task.getResult().get("recordUnit").toString()),
                            task.getResult().get("year").toString(),
                            task.getResult().get("month").toString(),
                            task.getResult().get("houseNo").toString(),
                            task.getResult().get("signature").toString(),
                            Integer.parseInt(task.getResult().get("recordNo").toString()),
                            Integer.parseInt(task.getResult().get("price").toString())
                    );
//                    Log.d("overall_2",
//                    "House No." + record.getHouseNo() + " " +
//                            "Units = " + record.getRecordUnit() + " " +
//                            "Unit Price = " + record.getPrice() + " " +
//                            "Amount = " + intToStr(record.getPrice())
//                    );

                    waterRecords.add(record);
                    Log.d("overall_2", "waterRecorda size = " + intToStr(waterRecords.size()));
                }
            }
        });
    }

    public String intToStr(int number) {
        return Integer.toString(number);
    }

    public void oldStyle() {
        firebaseFirestore
                .collection("rooms")
                .document("house_no 1")
                .collection("water_usage")
                .document("2018_2")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("overall", task.getResult().get("recordUnit").toString());
                }
                else {
                    Log.d("overall", "Error ngai");
                }
            }
        });
    }
}

//vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
//Old Style
//        colRef.document("house_no 1").collection("water_usage")
//                .document("2018_2").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    Log.d("overall", task.getResult().get("recordUnit").toString());
//                }
//                else {
//                    Log.d("overall", "Error ngai");
//                }
//            }
//        });
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA