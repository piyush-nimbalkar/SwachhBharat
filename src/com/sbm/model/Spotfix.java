package com.sbm.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Spotfix {

    private static final String PENDING = "pending";
    private static final String FIXED = "fixed";

    private long id;
    private long ownerId;
    private String title;
    private String description;
    private String status;
    private long estimatedHours;
    private long estimatedPeople;
    private double latitude;
    private double longitude;
    private Date fixDate;

    public Spotfix(long ownerId, String title, String description, long estimatedHours,
                   long estimatedPeople, double latitude, double longitude, Date fixDate) {
        this.ownerId = ownerId;
        this.title = title;
        this.description = description;
        this.status = PENDING;
        this.estimatedHours = estimatedHours;
        this.estimatedPeople = estimatedPeople;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fixDate = fixDate;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public long getEstimatedHours() {
        return estimatedHours;
    }

    public long getEstimatedPeople() {
        return estimatedPeople;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getFixDateInString() {
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS");
        return sdf.format(fixDate);
    }

}
