package com.example.redconnectlogo;

public class Donor {

    public String id;
    public String name;
    public String bloodGroup;
    public String phone;
    public String city;
    public String status;
    public double latitude;
    public double longitude;
    public boolean willing;
    public String state;
    public String district;

    public Donor() {}

    public Donor(String id, String name, String bloodGroup,
                 String phone, String city,
                 double latitude, double longitude) {

        this.id = id;
        this.name = name;
        this.bloodGroup = bloodGroup;
        this.phone = phone;
        this.city = city;
        this.status = "Available";
        this.latitude = latitude;
        this.longitude = longitude;
        this.willing = false;
    }
}