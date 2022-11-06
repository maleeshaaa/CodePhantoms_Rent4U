package com.example.carrentalapp_rent4u;

import java.sql.Time;
import java.util.Date;

public class Request {

private String vehicleType;
private String vehicleCategory;
private String pickUpdate;
private String timeTo;
private String timeFrom;

    public Request() {
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleCategory() {
        return vehicleCategory;
    }

    public void setVehicleCategory(String vehicleCategory) {
        this.vehicleCategory = vehicleCategory;
    }

    public String getPickUpdate() {
        return pickUpdate;
    }

    public void setPickUpdate(String pickUpdate) {
        this.pickUpdate = pickUpdate;
    }

    public String getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(String timeTo) {
        this.timeTo = timeTo;
    }

    public String getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(String timeFrom) {
        this.timeFrom = timeFrom;
    }
}
