package com.kmitl.vpower.waterusagechecker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class WaterRecordAdapter extends ArrayAdapter<WaterRecord> {
    private List<WaterRecord> waterRecords;
    private Context context;

    public WaterRecordAdapter(@NonNull Context context,
                              int resource,
                              @NonNull List<WaterRecord> objects) {
        super(context, resource, objects);
        this.context = context;
        this.waterRecords = objects;
    }

    @NonNull
    @Override
    public View getView(int position,
                        @Nullable View convertView,
                        @NonNull ViewGroup parent) {

        View recordItem = LayoutInflater.from(context).inflate(
                R.layout.fragment_overall_item,
                parent,
                false);

        TextView roomText = (TextView) recordItem.findViewById(R.id.fragment_overall_row_room);
        TextView unitsText = (TextView) recordItem.findViewById(R.id.fragment_overall_row_units);
        TextView unitPriceText = (TextView) recordItem.findViewById(R.id.fragment_overall_row_unit_price);
        TextView amountText = (TextView) recordItem.findViewById(R.id.fragment_overall_row_amount);

        WaterRecord record = waterRecords.get(position);
        String totalPrice = Integer.toString(record.getRecordUnit() * record.getPrice());

        Log.d("adapter", "setThreeColumnINaRow");
        roomText.setText(record.getHouseNo());
        unitsText.setText(Integer.toString(record.getRecordUnit()));
        unitPriceText.setText(Integer.toString(record.getPrice()));
        amountText.setText(totalPrice);

        return recordItem;
    }
}
