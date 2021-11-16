package com.example.medi_android;

import androidx.annotation.NonNull;

public class HeartDiseaseData {
    float chestPainType;
    float restingBloodPressure;
    float serumCholesterol;
    float fastingBloodSugar;
    float restingECG;
    float maxHeartRateAchieved;
    float exerciseInducedAngina;
    float STDepressionInduced;
    float peakExerciseSTSegment;
    float majorVesselsNumber;
    float thal;
    float age;
    float gender;

    public HeartDiseaseData() {
    }

    public float getChestPainType() {
        return chestPainType;
    }

    public void setChestPainType(float chestPainType) {
        this.chestPainType = chestPainType;
    }

    public float getRestingBloodPressure() {
        return restingBloodPressure;
    }

    public void setRestingBloodPressure(float restingBloodPressure) {
        this.restingBloodPressure = restingBloodPressure;
    }

    public float getSerumCholesterol() {
        return serumCholesterol;
    }

    public void setSerumCholesterol(float serumCholesterol) {
        this.serumCholesterol = serumCholesterol;
    }

    public float getFastingBloodSugar() {
        return fastingBloodSugar;
    }

    public void setFastingBloodSugar(float fastingBloodSugar) {
        this.fastingBloodSugar = fastingBloodSugar;
    }

    public float getRestingECG() {
        return restingECG;
    }

    public void setRestingECG(float restingECG) {
        this.restingECG = restingECG;
    }

    public float getMaxHeartRateAchieved() {
        return maxHeartRateAchieved;
    }

    public void setMaxHeartRateAchieved(float maxHeartRateAchieved) {
        this.maxHeartRateAchieved = maxHeartRateAchieved;
    }

    public float getExerciseInducedAngina() {
        return exerciseInducedAngina;
    }

    public void setExerciseInducedAngina(float exerciseInducedAngina) {
        this.exerciseInducedAngina = exerciseInducedAngina;
    }

    public float getSTDepressionInduced() {
        return STDepressionInduced;
    }

    public void setSTDepressionInduced(float STDepressionInduced) {
        this.STDepressionInduced = STDepressionInduced;
    }

    public float getPeakExerciseSTSegment() {
        return peakExerciseSTSegment;
    }

    public void setPeakExerciseSTSegment(float peakExerciseSTSegment) {
        this.peakExerciseSTSegment = peakExerciseSTSegment;
    }

    public float getMajorVesselsNumber() {
        return majorVesselsNumber;
    }

    public void setMajorVesselsNumber(float majorVesselsNumber) {
        this.majorVesselsNumber = majorVesselsNumber;
    }

    public float getThal() {
        return thal;
    }

    public void setThal(float thal) {
        this.thal = thal;
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

    @NonNull
    @Override
    public String toString() {
        return "HeartDiseaseData{" +
                "chestPainType=" + chestPainType +
                ", restingBloodPressure=" + restingBloodPressure +
                ", serumCholesterol=" + serumCholesterol +
                ", fastingBloodSugar=" + fastingBloodSugar +
                ", restingECG=" + restingECG +
                ", maxHeartRateAchieved=" + maxHeartRateAchieved +
                ", exerciseInducedAngina=" + exerciseInducedAngina +
                ", STDepressionInduced=" + STDepressionInduced +
                ", peakExerciseSTSegment=" + peakExerciseSTSegment +
                ", majorVesselsNumber=" + majorVesselsNumber +
                ", thal=" + thal +
                ", age=" + age +
                ", gender=" + gender +
                '}';
    }
}
