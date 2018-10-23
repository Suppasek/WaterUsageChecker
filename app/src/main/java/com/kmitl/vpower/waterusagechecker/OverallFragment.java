package com.kmitl.vpower.waterusagechecker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OverallFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<String> monthList = new ArrayList<>();
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
        createMothSpinner();
        getValueDB();
    }

    private void createMothSpinner() {
        Spinner dateSpinner = getView().findViewById(R.id.fragment_overall_date_spinner);
        for (Integer i = 1; i < 13; i++) {
            monthList.add("2018_" + intToStr(i));
        }

        ArrayAdapter<String> adapterMonth = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, monthList);
        dateSpinner.setAdapter(adapterMonth);
    }

    public void getValueDB() {
        Log.d("overall", "In getValueDB");
        String recDate = "2018_2";

        ListView recordTable = (ListView) getView().findViewById(R.id.fragment_overall_list);


        final WaterRecordAdapter recordAdapter = new WaterRecordAdapter(getActivity(), R.layout.fragment_overall_item, waterRecords);

        recordTable.setAdapter(recordAdapter);
        waterRecords.clear();

        for (int i = 1; i < 4; i++ ) {
            Log.d("overall", "Before Loop " + intToStr(i));

            firebaseFirestore
                    .collection("rooms")
                    .document("house_no " + intToStr(i))
                    .collection("water_usage")
                    .document(recDate)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
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
                    }
                    Log.d("overall3", intToStr(waterRecords.size()));
                    Log.d("overall3", "House No. = " + waterRecords.get(0).getHouseNo());
                }
            });
        }

        Log.d("overall", "End getValueDB");
    }

    public void getRecordFromHouses(final String houseNo, final String recDate, final WaterRecordAdapter recordAdapter) {
        Log.d("overall", "In getRecordFromHouse\nhouseNo = " + houseNo + " recDate = " + recDate);
    }

    public String intToStr(int number) {
        return Integer.toString(number);
    }

}