package com.example.misteryshopper.models;

import java.time.LocalDate;

public class HiringModel {
    String id;
    String idEmployer;
    String employerName;
    String mailShopper;
    String idStore;
    String date;
    double fee;
    String accepted;
    boolean done;

    public HiringModel() {
    }

    public HiringModel(String id, String idEmployer, String employerName, String mailShopper, String idStore,String date, double fee) {
        this.id = id;
        this.idEmployer = idEmployer;
        this.mailShopper = mailShopper;
        this.idStore = idStore;
        this.date = date;
        this.fee = fee;
        this.employerName = employerName;
        done = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdEmployer() {
        return idEmployer;
    }

    public void setIdEmployer(String idEmployer) {
        this.idEmployer = idEmployer;
    }

    public String getEmployerName() {
        return employerName;
    }

    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }

    public String getMailShopper() {
        return mailShopper;
    }

    public void setMailShopper(String mailShopper) {
        this.mailShopper = mailShopper;
    }

    public String getIdStore() {
        return idStore;
    }

    public void setIdStore(String idStore) {
        this.idStore = idStore;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public String isAccepted() {
        return accepted;
    }

    public void setAccepted(String accepted) {
        this.accepted = accepted;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
