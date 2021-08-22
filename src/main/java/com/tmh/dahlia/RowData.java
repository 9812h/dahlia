package com.tmh.dahlia;

public class RowData {
    private Integer num;
    private String time;
    private Float price;
    private Float change;
    private Float qtty;
    private String BorS;

    public RowData(Integer num, String time, Float price, Float change, Float qtty, String borS) {
        this.num = num;
        this.time = time;
        this.price = price;
        this.change = change;
        this.qtty = qtty;
        this.BorS = borS;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getChange() {
        return change;
    }

    public void setChange(Float change) {
        this.change = change;
    }

    public Float getQtty() {
        return qtty;
    }

    public void setQtty(Float qtty) {
        this.qtty = qtty;
    }

    public String getBorS() {
        return BorS;
    }

    public void setBorS(String borS) {
        BorS = borS;
    }

    public String toString() {
        final String SEP = " | ";
        return SEP + num + SEP + time + SEP + price + SEP + change + SEP + qtty + SEP + BorS + SEP;
    }

    public String getNotifString() {
        return "Time: " + time;
    }
}
