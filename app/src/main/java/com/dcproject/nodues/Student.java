package com.dcproject.nodues;

/**
 * Created by SAI on 27-03-2018.
 */

public class Student {
    private String name;
    private String rollno;
    private String year;
    private String branch;
    private String programme;
    private String email;
    private String phone;

    public Student(){

    }

    public void setname(String name){this.name=name;}

    public void setrollno(String rollno){this.rollno=rollno;}

    public void setbranch(String branch){this.branch= branch;}

    public void setprogramme(String programme){this.programme=programme;}

    public void setyear(String year){this.year= year;}

    public void setemail(String email) {this.email= email;}

    public void setPhone(String phone){this.phone=phone;}

    public Student(String branch,String email,String name,String programme,String year,String Phone){
        this.name=name;
        this.branch=branch;
        this.programme=programme;
        this.year=year;
        this.email=email;
        this.phone=Phone;
    }

    public String getname(){return name;}

    public String getrollno(){return rollno;}

    public String getbranch(){return branch;}

    public String getprogramme(){return programme;}

    public String getyear(){return year;}

    public String getemail() {return email;}

    public String getPhone() {return phone;}



}
