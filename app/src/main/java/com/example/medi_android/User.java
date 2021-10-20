package com.example.medi_android;

public class User {
    public String email;
    public String name;
    public String dob;
    public String gender;
    public String height;
    public String weight;
    public Review review;
    public String role;

    public User(){

    }

    public User(String email){
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole() {
        this.role = "Patient";
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
