package com.example.wildfiredetection;

public class Fire {
    String location;
    String acq_date;
    String acq_time;
    Double latitude;
    Double longitude;


    public Fire(String location, String acq_date,
                     String acq_time, Double latitude, Double longitude) {
        this.location = location;
        this.acq_date = acq_date;
        this.acq_time = acq_time;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public String getAcq_date() {
        return acq_date;
    }

    public String getAcq_time() {
        return acq_time;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
