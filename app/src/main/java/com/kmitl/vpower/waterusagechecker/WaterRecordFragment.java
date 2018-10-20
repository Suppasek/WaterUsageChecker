package com.kmitl.vpower.waterusagechecker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class WaterRecordFragment extends Fragment {

    private Spinner monthSpinner;
    private Spinner yearSpinner;
    private Spinner roomSpinner;

    private ProgressBar progressBar;
    private FrameLayout progressScreen;

    private FirebaseFirestore mdb = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private CollectionReference colRef;

    private ArrayList<String> monthList = new ArrayList<>();
    private ArrayList<String> yearList = new ArrayList<>();
    private ArrayList<String> roomList = new ArrayList<>();

    private WaterRecord record;

    private String unitStr;
    private String room;
    private String month;
    private String year;
    private int recordNo;
    private int rate;
    private int previousUnit;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_water_record, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        monthSpinner = getView().findViewById(R.id.water_record_month);
        yearSpinner = getView().findViewById(R.id.water_record_year);
        roomSpinner = getView().findViewById(R.id.water_record_room);

        progressBar = getView().findViewById(R.id.water_record_progressBar);
        progressScreen = getView().findViewById(R.id.water_record_progressBarHolder);

        createData();

        ArrayAdapter<String> adapterMonth = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, monthList);
        monthSpinner.setAdapter(adapterMonth);

        ArrayAdapter<String> adapterYear = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, yearList);
        yearSpinner.setAdapter(adapterYear);

        ArrayAdapter<String> adapterRoom = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, roomList);
        roomSpinner.setAdapter(adapterRoom);

        getRate();

//        roomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                getPreviousRecord();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        initAddBtn();

    }

    private void initAddBtn() {
        Button btnAdd = getView().findViewById(R.id.water_record_add);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText meter = getView().findViewById(R.id.water_record_meter);
                unitStr = meter.getText().toString();

                month = monthSpinner.getSelectedItem().toString();
                year = yearSpinner.getSelectedItem().toString();

                setProgressBar(true);

                getPreviousRecord();


            }
        });
    }

    private void getPreviousRecord() {
        room = roomSpinner.getSelectedItem().toString();
        colRef = mdb.collection("rooms").document("house_no " + room).collection("water_usage");
        colRef.whereEqualTo("year", year).whereEqualTo("month", month).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    recordNo = Integer.parseInt(task.getResult().getDocuments().get(0).get("recordNo").toString());
                    previousUnit = Integer.parseInt(task.getResult().getDocuments().get(0).get("recordUnit").toString());
                    pushRecord();
                    Log.wtf("WRF", "record = " + recordNo + " edit previous record");
                } else if (task.isSuccessful()) {
                    colRef.orderBy("recordNo", Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (!task.getResult().isEmpty()) {
                                    recordNo = Integer.parseInt(task.getResult().getDocuments().get(0).get("recordNo").toString()) + 1;
                                    previousUnit = Integer.parseInt(task.getResult().getDocuments().get(0).get("recordUnit").toString());
                                    pushRecord();
                                    Log.wtf("WRF", "record = " + recordNo + " new record");
                                } else if (task.isSuccessful()) {
                                    recordNo = 1;
                                    previousUnit = 0;
                                    pushRecord();
                                    Log.wtf("WRF", "record = " + recordNo + " new record new collection");
                                }
                            }

                        }
                    });
                }
            }
        });
    }

    private void getRate() {
        setProgressBar(true);
        mdb.collection("Water Rates").orderBy("recordNo", Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    rate = Integer.parseInt(task.getResult().getDocuments().get(0).get("rate").toString());
                    setProgressBar(false);
                }
            }
        });
    }

    private void pushRecord() {
        String monthFormat = year + "_" + month;
        int unit = Integer.parseInt(unitStr);
        int price;
        if (previousUnit != 0) {
            price = (unit - previousUnit) * rate;
        }
        else {
            price = 0;
        }
        record = new WaterRecord(unit, year, month, room, user.getDisplayName(), recordNo, price);
        mdb.collection("rooms")
                .document("house_no " + room)
                .collection("water_usage")
                .document(monthFormat)
                .set(record).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.wtf("WRF", "month " + month + " year " + year + " record no " + recordNo);
                setProgressBar(false);
                Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Failure", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setProgressBar(Boolean on) {
        if (on) {
            progressBar.setVisibility(View.VISIBLE);
            progressScreen.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            progressScreen.setVisibility(View.GONE);
        }

    }

    private void createData() {
        for (Integer i = 1; i < 13; i++) {
            monthList.add(i.toString());
        }
        for (Integer i = 2018; i < 2038; i++) {
            yearList.add(i.toString());
        }
        for (Integer i = 1; i < 21; i++) {
            roomList.add(i.toString());
        }
    }

}
