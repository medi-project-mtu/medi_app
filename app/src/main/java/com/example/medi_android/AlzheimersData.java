package com.example.medi_android;

import java.io.Serializable;

public class AlzheimersData implements Serializable {
    float dominantHand;
    float educationLevel;
    float socialEconomicStatus;
    float miniMentalStateExamination;
    float clinicalDementiaRating;
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

    public float getDominantHand() {
        return dominantHand;
    }

    public void setDominantHand(float dominantHand) {
        this.dominantHand = dominantHand;
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

    public float getClinicalDementiaRating() {
        return clinicalDementiaRating;
    }

    public void setClinicalDementiaRating(float clinicalDementiaRating) {
        this.clinicalDementiaRating = clinicalDementiaRating;
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

    @Override
    public String toString() {
        return "AlzheimersData{" +
                "dominantHand=" + dominantHand +
                ", educationLevel=" + educationLevel +
                ", socialEconomicStatus=" + socialEconomicStatus +
                ", miniMentalStateExamination=" + miniMentalStateExamination +
                ", clinicalDementiaRating=" + clinicalDementiaRating +
                ", estimatedTotalIntracranialVolume=" + estimatedTotalIntracranialVolume +
                ", normalizeHoleBrainVolume=" + normalizeHoleBrainVolume +
                ", age=" + age +
                ", gender=" + gender +
                ", diagnosis='" + diagnosis + '\'' +
                '}';
    }

    public void setGender(float gender) {
        this.gender = gender;
    }
}
