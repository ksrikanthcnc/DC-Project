package com.dcproject.nodues;


public class Payments {
    private String transactionid;
    private int amount;
    private String date;

    public Payments(){
    }

    public Payments(String id,int amt,String date){
        this.transactionid=id;
        this.amount=amt;
        this.date=date;
    }

    public String getTransactionid(){return transactionid;}


    public int getamount(){return amount;}

    public String getDate(){return date;}

}
