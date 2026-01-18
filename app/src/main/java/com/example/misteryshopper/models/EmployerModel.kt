package com.example.misteryshopper.models;

import java.util.ArrayList;
import java.util.List;

public class EmployerModel extends User{

    String emName;
    String category;
    String pIva;



    public EmployerModel(String emName, String category, String pIva,String id,String eMail,String role,String token) {
        super(eMail,id, role,token);
        this.emName = emName;
        this.category = category;
        this.pIva = pIva;

    }

    public EmployerModel() {
    }

    public String getEmName() {
        return emName;
    }

    public void setEmName(String emName) {
        this.emName = emName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getpIva() {
        return pIva;
    }

    public void setpIva(String pIva) {
        this.pIva = pIva;
    }


    @Override
    public String toString() {
        return "EmployerModel{" +
                "emName='" + emName + '\'' +
                ", category='" + category + '\'' +
                ", pIva='" + pIva + '\'' +
                ", email='" + email + '\'' +
                ", id='" + id + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
