package edu.ncsu.csc.itrust.beans;

import java.io.Serializable;

public class PreRegisterBean implements Serializable
{
    private PatientBean patient;
    private String height;
    private String weight;
    private String smoker;
    private long mid;

    public String getHeight() {return height;}

    public String getSmoker() {
        return smoker;
    }

    public long getMid() {
        return mid;
    }

    public PatientBean getPatient() {
        return patient;
    }

    public String getWeight() {
        return weight;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setSmoker(String smoker) {
        this.smoker = smoker;
    }

    public void setPatient(PatientBean patient) {
        this.patient = patient;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setMid(long mid) {
        this.mid = mid;
    }
}
