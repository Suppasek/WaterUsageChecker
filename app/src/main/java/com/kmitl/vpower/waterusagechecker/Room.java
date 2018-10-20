package com.kmitl.vpower.waterusagechecker;

public class Room {
    private String houseNo;
    private String description;
    private String recordUnit;
    private int price;
    private long amount;

    public Room() {
    }

    public Room(String houseNo, String description, String recordUnit, int price, long amount) {
        this.houseNo = houseNo;
        this.description = description;
        this.recordUnit = recordUnit;
        this.price = price;
        this.amount = amount;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRecordUnit() {
        return recordUnit;
    }

    public void setRecordUnit(String recordUnit) {
        this.recordUnit = recordUnit;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
