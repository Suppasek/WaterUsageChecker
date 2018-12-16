package com.kmitl.vpower.waterusagechecker;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class WaterViewHolder extends RecyclerView.ViewHolder {

    TextView month, price, unit, meter, totalUnit;

    public WaterViewHolder(@NonNull View itemView) {
        super(itemView);

        month = itemView.findViewById(R.id.water_bill_month);
        price = itemView.findViewById(R.id.water_bill_price);
        meter = itemView.findViewById(R.id.water_bill_meter);
        totalUnit = itemView.findViewById(R.id.water_bill_total_unit);
    }
}
