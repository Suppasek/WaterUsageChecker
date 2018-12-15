package com.kmitl.vpower.waterusagechecker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class WaterBillRecordFragment extends Fragment {

    private String room;
    private ArrayList<String> yearList = new ArrayList<>();
    private ArrayList<WaterRecord> bills = new ArrayList<>();
    private Spinner yearSpinner;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private RecyclerView billList;
    WaterBillAdapter waterBillAdapter = new WaterBillAdapter();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_water_bill, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getRoom();
        createData();
        assignId();
        setRecyclerView();
        setSpinner();
        setOnYearSelected();
    }

    private void getRoom() {
        room = getArguments().getString("room");
    }

    private void createData() {
        for (Integer i = 2018; i < 2038; i++) {
            yearList.add(i.toString());
        }
    }

    private void assignId() {
        yearSpinner = getView().findViewById(R.id.water_bill_year_spinner);
        billList = getView().findViewById(R.id.water_bill_list);
    }

    private void setRecyclerView() {
        billList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        waterBillAdapter.setItemList(bills);
        billList.setAdapter(waterBillAdapter);
    }

    private void setSpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, yearList);
        yearSpinner.setAdapter(spinnerAdapter);
    }

    private void setOnYearSelected() {
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getData(yearList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getData(String year) {
        firestore.collection("rooms")
                .document("house_no " + room)
                .collection("water_usage")
//                .whereEqualTo("year", year)
//                .orderBy("month")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot bill : task.getResult()) {
                            bills.add(bill.toObject(WaterRecord.class));
                        }
                        waterBillAdapter.notifyDataSetChanged();
                    }
                });
    }

}
