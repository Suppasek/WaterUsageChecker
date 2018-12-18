package com.kmitl.vpower.waterusagechecker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WaterRecord {

    private int recordNo;
    private int recordUnit;
    private int price;
    private int totalUnit;
    private String recordDate;
    private String year;
    private String month;
    private String houseNo;
    private String signature;

    public WaterRecord() {

    }

    public WaterRecord(int recordUnit, String year, String month, String houseNo, String signature, int recordNo, int price, int totalUnit) {
        setRecordUnit(recordUnit);
        setRecordDate(new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(Calendar.getInstance().getTime()));
        setYear(year);
        setMonth(month);
        setSignature(signature);
        setHouseNo(houseNo);
        setRecordNo(recordNo);
        setPrice(price);
        setTotalUnit(totalUnit);
    }

    public WaterRecord(int recordUnit, String year, String month, String houseNo, String signature, int recordNo, int price) {
        setRecordUnit(recordUnit);
        setRecordDate(new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(Calendar.getInstance().getTime()));
        setYear(year);
        setMonth(month);
        setSignature(signature);
        setHouseNo(houseNo);
        setRecordNo(recordNo);
        setPrice(price);
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getTotalUnit() {
        return totalUnit;
    }

    public void setTotalUnit(int totalUnit) {
        this.totalUnit = totalUnit;
    }

    public int compare2To(WaterRecord waterRecord, boolean isHouse) {
        if (isHouse) {
            Integer temp1 = Integer.parseInt(this.houseNo);
            Integer temp2 = Integer.parseInt(waterRecord.getHouseNo());
            return temp1.compareTo(temp2);
        }
        else {
            Integer temp1 = Integer.parseInt(this.month);
            Integer temp2 = Integer.parseInt(waterRecord.getMonth());
            return temp1.compareTo(temp2);
        }
    }
}
