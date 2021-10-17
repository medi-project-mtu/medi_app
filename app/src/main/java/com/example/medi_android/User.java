package com.example.medi_android;

public class User {
    public String email;
    public String fname;
    public String lname;
    public String dob;
    public String phone;

    public User(){

    }

    public User(String email){
        this.email = email;
    }

    public User(String email, String fname, String lname, String dob, String phone){
        this.email = email;
        this.fname = fname;
        this.lname = lname;
        this.dob = dob;
        this.phone = phone;
    }

}
