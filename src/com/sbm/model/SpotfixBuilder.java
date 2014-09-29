package com.sbm.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SpotfixBuilder {

    public long id;
    public long ownerId;
    public String title;
    public String description;
    public String status;
    public long estimatedHours;
    public long estimatedPeople;
    public double latitude;
    public double longitude;
    public Date fixDate;

    public static SpotfixBuilder spotfix() {
        return new SpotfixBuilder();
    }

    public Spotfix build() {
        this.status = Spotfix.PENDING;
        return new Spotfix(this);
    }

    public SpotfixBuilder setId(long id) {
        this.id = id;
        return this;
    }

    public SpotfixBuilder setOwnerId(long ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    public SpotfixBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public SpotfixBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public SpotfixBuilder setStatus(String status) {
        this.status = status;
        return this;
    }

    public SpotfixBuilder setEstimatedHours(long estimatedHours) {
        this.estimatedHours = estimatedHours;
        return this;
    }

    public SpotfixBuilder setEstimatedPeople(long estimatedPeople) {
        this.estimatedPeople = estimatedPeople;
        return this;
    }

    public SpotfixBuilder setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public SpotfixBuilder setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public SpotfixBuilder setFixDate(Date fixDate) {
        this.fixDate = fixDate;
        return this;
    }

    public SpotfixBuilder setFixDate(String fixDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(Spotfix.DATE_FORMAT);
        this.fixDate = sdf.parse(fixDate);
        return this;
    }

}
