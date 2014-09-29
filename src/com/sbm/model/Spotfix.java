package com.sbm.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Spotfix {

    public static final String PENDING = "pending";
    public static final String FIXED = "fixed";

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

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

    public Spotfix(SpotfixBuilder builder) {
        id = builder.id;
        ownerId = builder.ownerId;
        title = builder.title;
        description = builder.description;
        status = builder.status;
        estimatedHours = builder.estimatedHours;
        estimatedPeople = builder.estimatedPeople;
        latitude = builder.latitude;
        longitude = builder.longitude;
        fixDate = builder.fixDate;
    }

    public long getId() {
        return id;
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
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(fixDate);
    }

}
