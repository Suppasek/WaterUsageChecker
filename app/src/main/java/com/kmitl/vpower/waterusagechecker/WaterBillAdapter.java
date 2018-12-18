package com.kmitl.vpower.waterusagechecker;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class WaterBillAdapter extends RecyclerView.Adapter<WaterViewHolder> {

    ArrayList<WaterRecord> waterRecords = new ArrayList<>();
    private ContextCompat resources;

    @NonNull
    @Override
    public WaterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i== 1) {
            // inflate your first item layout & return that viewHolder
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_water_record, viewGroup, false);
            WaterViewHolder viewHolder = new WaterViewHolder(view);
            viewHolder.border.setBackgroundResource(R.drawable.bglist_ch);
            viewHolder.border2.setBackgroundResource(R.drawable.borderlist_ch);
            return viewHolder;

        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_water_record, viewGroup, false);
            WaterViewHolder viewHolder = new WaterViewHolder(view);
            return viewHolder;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 1;
        else return 2;
    }

    @Override
    public void onBindViewHolder(@NonNull WaterViewHolder waterViewHolder, int i) {
        final WaterRecord waterRecord = waterRecords.get(i);


        ArrayList<String> month = new ArrayList<>();
        month.add("มกราคม");
        month.add("กุมภาพันธ์");
        month.add("มีนาคม");
        month.add("เมษายน");
        month.add("พฤษภาคน");
        month.add("มิถุนายน");
        month.add("กรกฏาคม");
        month.add("สิงหาคม");
        month.add("กันยายน");
        month.add("ตุลาคม");
        month.add("พฤศจิกายน");
        month.add("ธันวาคม");

        waterViewHolder.month.setText(month.get(Integer.parseInt(waterRecord.getMonth())-1));
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
