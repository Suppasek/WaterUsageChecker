package com.kmitl.vpower.waterusagechecker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WaterRecord {

    private int waterMeter;
    private String recordDate;
    private String year;
    private String month;
    private String room;

    public WaterRecord(int waterMeter, String year, String month, String room) {
        this.setWaterMeter(waterMeter);
        setRecordDate(new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(Calendar.getInstance().getTime()));
        this.setYear(year);
        this.setMonth(month);
        this.setRoom(room);
    }

    public int getWaterMeter() {
        return waterMeter;
    }

    public void setWaterMeter(int waterMeter) {
        this.waterMeter = waterMeter;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
