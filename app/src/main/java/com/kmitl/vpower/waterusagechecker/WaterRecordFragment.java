package com.kmitl.vpower.waterusagechecker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class WaterRecordFragment extends Fragment {

    private Spinner monthSpinner;
    private Spinner yearSpinner;
    private Spinner roomSpinner;

    private FirebaseFirestore mdb;

    private ArrayList<String> monthList = new ArrayList<>();
    private ArrayList<String> yearList = new ArrayList<>();
    private ArrayList<String> roomList = new ArrayList<>();

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

        createData();

        ArrayAdapter<String> adapterMonth = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, monthList);
        monthSpinner.setAdapter(adapterMonth);

        ArrayAdapter<String> adapterYear = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, yearList);
        yearSpinner.setAdapter(adapterYear);

        ArrayAdapter<String> adapterRoom = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, roomList);
        roomSpinner.setAdapter(adapterRoom);

        initAddBtn();

    }

    private void initAddBtn() {
        Button btnAdd = getView().findViewById(R.id.water_record_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText meter = getView().findViewById(R.id.water_record_meter);
                String meterStr = meter.getText().toString();

                String month  = monthSpinner.getSelectedItem().toString();
                String year  = yearSpinner.getSelectedItem().toString();
                String room  = roomSpinner.getSelectedItem().toString();

                WaterRecord record = new WaterRecord(Integer.parseInt(meterStr), year, month, room);

                String monthFormat = year + "-" + month;

                mdb = FirebaseFirestore.getInstance();
                mdb.collection("water_record").document(monthFormat).collection("room "+room).add(record).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Fail", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }

    private void createData() {
        for  (Integer i = 1;i < 13;i++) {
            monthList.add(i.toString());
        }
        for  (Integer i = 2561;i < 2581;i++) {
            yearList.add(i.toString());
        }
        for  (Integer i = 1;i < 21;i++) {
            roomList.add(i.toString());
        }
    }

}
