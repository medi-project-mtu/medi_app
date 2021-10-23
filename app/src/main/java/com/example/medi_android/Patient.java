package com.example.medi_android;

public class Patient {
    private String email;
    private String name;
    private String dob;
    private String gender;
    private String height;
    private String weight;
    private Review review;
    private String gpUid;


    public String getGpUid() {
        return gpUid;
    }

    public void setGpUid(String gpUid) {
        this.gpUid = gpUid;
    }

    public Patient(){

    }

    public Patient(String email){
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public Review getReview(){
        return review;

    }
    public void setReview(Review review){
        this.review =review;
    }
}
