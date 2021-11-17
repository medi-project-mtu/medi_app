package com.example.medi_android;

import java.io.Serializable;

public class DiabetesData implements Serializable {
    float pregnancies;
    float glucose;
    float bloodPressure;
    float skinThickness;
    float insulin;
    float bmi;
    float diabetesPedigreeFunction;
    float age;
    String diagnosis;

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public float getPregnancies() {
        return pregnancies;
    }

    public void setPregnancies(float pregnancies) {
        this.pregnancies = pregnancies;
    }

    public float getGlucose() {
        return glucose;
    }

    public void setGlucose(float glucose) {
        this.glucose = glucose;
    }

    public float getBloodPressure() {
        return bloodPressure;
    }


    public void setBloodPressure(float bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public float getSkinThickness() {
        return skinThickness;
    }

    public void setSkinThickness(float skinThickness) {
        this.skinThickness = skinThickness;
    }

    public float getInsulin() {
        return insulin;
    }

    public void setInsulin(float insulin) {
        this.insulin = insulin;
    }

    public float getBmi() {
        return bmi;
    }

    public void setBmi(float bmi) {
        this.bmi = bmi;
    }

    public float getDiabetesPedigreeFunction() {
        return diabetesPedigreeFunction;
    }

    public void setDiabetesPedigreeFunction(float diabetesPedigreeFunction) {
        this.diabetesPedigreeFunction = diabetesPedigreeFunction;
    }

    public float getAge() {
        return age;
    }

    public void setAge(float age) {
        this.age = age;
    }

    public DiabetesData(){}

    @Override
    public String toString() {
        return "DiabetesData{" +
                "pregnancies=" + pregnancies +
                ", glucose=" + glucose +
                ", bloodPressure=" + bloodPressure +
                ", skinThickness=" + skinThickness +
                ", insulin=" + insulin +
                ", bmi=" + bmi +
                ", diabetesPedigreeFunction=" + diabetesPedigreeFunction +
                ", age=" + age +
                ", diagnosis='" + diagnosis + '\'' +
                '}';
    }
}
