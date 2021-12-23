package com.example.medi_android;

import java.io.Serializable;

public class AlzheimersData implements Serializable {
    float educationLevel;
    float socialEconomicStatus;
    float miniMentalStateExamination;
    float asf;
    float estimatedTotalIntracranialVolume;
    float normalizeHoleBrainVolume;
    float age;
    float gender;
    String diagnosis;

    public AlzheimersData() {
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public float getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(float educationLevel) {
        this.educationLevel = educationLevel;
    }

    public float getSocialEconomicStatus() {
        return socialEconomicStatus;
    }

    public void setSocialEconomicStatus(float socialEconomicStatus) {
        this.socialEconomicStatus = socialEconomicStatus;
    }

    public float getMiniMentalStateExamination() {
        return miniMentalStateExamination;
    }

    public void setMiniMentalStateExamination(float miniMentalStateExamination) {
        this.miniMentalStateExamination = miniMentalStateExamination;
    }

    public float getAsf() {
        return asf;
    }

    public void setAsf(float asf) {
        this.asf = asf;
    }

    public float getEstimatedTotalIntracranialVolume() {
        return estimatedTotalIntracranialVolume;
    }

    public void setEstimatedTotalIntracranialVolume(float estimatedTotalIntracranialVolume) {
        this.estimatedTotalIntracranialVolume = estimatedTotalIntracranialVolume;
    }

    public float getNormalizeHoleBrainVolume() {
        return normalizeHoleBrainVolume;
    }

    public void setNormalizeHoleBrainVolume(float normalizeHoleBrainVolume) {
        this.normalizeHoleBrainVolume = normalizeHoleBrainVolume;
    }

    public float getAge() {
        return age;
    }

    public void setAge(float age) {
        this.age = age;
    }

    public float getGender() {
        return gender;
    }

    public void setGender(float gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "AlzheimersData{" +
                "educationLevel=" + educationLevel +
                ", socialEconomicStatus=" + socialEconomicStatus +
                ", miniMentalStateExamination=" + miniMentalStateExamination +
                ", ASF=" + asf +
                ", estimatedTotalIntracranialVolume=" + estimatedTotalIntracranialVolume +
                ", normalizeHoleBrainVolume=" + normalizeHoleBrainVolume +
                ", age=" + age +
                ", gender=" + gender +
                ", diagnosis='" + diagnosis + '\'' +
                '}';
    }
}
