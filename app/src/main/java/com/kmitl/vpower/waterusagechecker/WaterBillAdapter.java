package com.kmitl.vpower.waterusagechecker;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class WaterBillAdapter extends RecyclerView.Adapter<WaterViewHolder> {

    ArrayList<WaterRecord> waterRecords = new ArrayList<>();

    @NonNull
    @Override
    public WaterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_water_record, viewGroup, false);
        return new WaterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WaterViewHolder waterViewHolder, int i) {
        final WaterRecord waterRecord = waterRecords.get(i);
        waterViewHolder.month.setText(waterRecord.getMonth());
        waterViewHolder.price.setText(Integer.toString(waterRecord.getPrice()));
        waterViewHolder.meter.setText(Integer.toString(waterRecord.getRecordUnit()));
        waterViewHolder.totalUnit.setText(Integer.toString(waterRecord.getTotalUnit()));

    }

    @Override
    public int getItemCount() {
        return waterRecords.size();
    }

    public void setItemList(ArrayList<WaterRecord> waterRecords) {
        this.waterRecords = waterRecords;
    }
}
