package com.dcproject.nodues;


import java.sql.Timestamp;
import java.util.Date;

public class Dues {
    private String id;
    private String reason;
    public int due;
    private int remaining;
    private String month;
    String date;

    public Dues(){

    }

    public void setid(String id){this.id=id;}

    public void setdue(Integer due){this.due=due;}

    public void setreason(String reason){this.reason=reason;}

    public void setMonth(String month){this.month=month;}

    public void setRemaining(int remaining) {this.remaining=remaining;}

    public Dues(String reason, int due, String date,String month){
        this.reason=reason;
        this.due=due;
        this.date=date;
        this.month=month;
        this.remaining=due;
    }

    public Dues(String reason, int due, String date){
        this.reason=reason;
        this.due=due;
        this.date=date;
    }

    public String getreason(){return reason;}

    public Integer getdue(){return due;}

    public String getMonth() {return month;}

    public Integer getRemaining() {return remaining;}

}
