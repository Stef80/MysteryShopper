package com.example.misteryshopper.models;

import java.io.Serializable;

public class StoreModel implements Serializable {


    String idStore;
    String idEmployer;
    String eName;
    String manager;
    String city;
    String address;
    String imageUri;

    public StoreModel() {
    }

    public String getIdStore() {
        return idStore;
    }

    public void setIdStore(String idStore) {
        this.idStore = idStore;
    }

    public String getManager() {
        return manager;
    }

    public String getIdEmployer() {
        return idEmployer;
    }

    public void setIdEmployer(String idEmployer) {
        this.idEmployer = idEmployer;
    }

    public String geteName() {
        return eName;
    }

    public void seteName(String eName) {
        this.eName = eName;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    @Override
    public String toString() {
        return "StoreModel{" +
                "idStore='" + idStore + '\'' +
                ", idEmployer='" + idEmployer + '\'' +
                ", eName='" + eName + '\'' +
                ", manager='" + manager + '\'' +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                ", imageUri='" + imageUri + '\'' +
                '}';
    }
}
