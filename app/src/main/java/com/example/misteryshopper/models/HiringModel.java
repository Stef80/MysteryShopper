package com.example.misteryshopper.models;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;

public class HiringModel implements Comparable<HiringModel>{
    String id;
    String idEmployer;
    String employerName;
    String address;
    String mailShopper;
    String idStore;
    String date;
    double fee;
    String accepted;
    boolean done;

    public HiringModel() {
    }

    public HiringModel(String id, String idEmployer, String employerName, String mailShopper,String address, String idStore,String date, double fee) {
        this.id = id;
        this.idEmployer = idEmployer;
        this.mailShopper = mailShopper;
        this.idStore = idStore;
        this.address = address;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getAccepted() {
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public int compareTo(HiringModel o) {
        DateFormat format = SimpleDateFormat.getPatternInstance("dd/MM/YYYY");
        Date date1= null;
        Date date2 = null;
        try {
             date2 = format.parse(o.getDate());
             date1 = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1.compareTo(date2);
    }

}
