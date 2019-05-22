package com.dcproject.nodues;

/**
 * Created by M.Hemanth on 13-03-2018.
 */

public class Department {


    String name = null;
    private String email;
    boolean selected = false;

    public Department(){

    }

    public Department(String name, boolean selected) {
        super();
        this.name = name;
        this.selected = selected;
    }

    public Department(String name,String email){
        this.name=name;
        this.email=email;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getemail() {return email;}
    public void setemail(String email) {this.email=email;}

    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}