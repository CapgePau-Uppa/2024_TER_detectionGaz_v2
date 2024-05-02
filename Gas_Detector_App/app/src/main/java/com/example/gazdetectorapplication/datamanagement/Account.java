package com.example.gazdetectorapplication.datamanagement;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Account {
    private String name;
    private String surname;
    private String zoneId;
    private boolean isAdmin;
    private boolean isPMR;
    private boolean isColorBlind;
    private String id;

    public Account(){
        this.name="";
        this.surname="";
        this.zoneId="";
        this.isAdmin=false;
        this.isPMR=false;
        this.isColorBlind=false;
        this.id="000000";
    }

    public Account(JSONObject obj) throws JSONException {
        this.name = obj.getString("name");
        this.surname = obj.getString("surname");
        this.id = obj.getString("_id");
        this.zoneId=obj.getString("zone");
        this.isAdmin=obj.getBoolean("isAdmin");
        this.isPMR=false;
        this.isColorBlind=false;
    }
    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getZoneId() {
        return zoneId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean isPMR() {
        return isPMR;
    }

    public boolean isColorBlind() {
        return isColorBlind;
    }

    public String getId(){return this.id;}


    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setPMR(boolean PMR) {
        isPMR = PMR;
    }

    public void setColorBlind(boolean colorBlind) {
        isColorBlind = colorBlind;
    }

    public void setId(String id){this.id=id;}


}
