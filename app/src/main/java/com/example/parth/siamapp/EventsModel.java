package com.example.parth.siamapp;

import java.io.Serializable;

/**
 * Created by Parth on 15-01-2017.
 */

public class EventsModel implements Serializable {
    String date;
    String description;
    String eventName;
    String imageUrl;

    public EventsModel() {
    }

    public EventsModel(String name, String date, String image) {
        eventName = name;
        this.date = date;
        this.imageUrl = image;
    }

    public EventsModel(String name, String date, String image, String description) {
        eventName = name;
        this.date = date;
        this.imageUrl = image;
        this.description = description;
    }
}