package com.kmitl.vpower.waterusagechecker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WaterRecord {

    private int recordNo;
    private int recordUnit;
    private String recordDate;
    private String year;
    private String month;
    private String houseNo;
    private String signature;

    public WaterRecord() {

    }

    public WaterRecord(int recordUnit, String year, String month, String houseNo, String signature, int recordNo) {
        setRecordUnit(recordUnit);
        setRecordDate(new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(Calendar.getInstance().getTime()));
        setYear(year);
        setMonth(month);
        setSignature(signature);
        setHouseNo(houseNo);
        setRecordNo(recordNo);
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

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getRecordUnit() {
        return recordUnit;
    }

    public void setRecordUnit(int recordUnit) {
        this.recordUnit = recordUnit;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public int getRecordNo() {
        return recordNo;
    }

    public void setRecordNo(int recordNo) {
        this.recordNo = recordNo;
    }
}
