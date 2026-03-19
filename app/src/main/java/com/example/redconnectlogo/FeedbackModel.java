package com.example.redconnectlogo;

import java.util.Date;

public class FeedbackModel {

    private float rating;
    private String feedbackText;
    private Date timestamp;

    public FeedbackModel() {}

    public float getRating() { return rating; }
    public String getFeedbackText() { return feedbackText; }
    public Date getTimestamp() { return timestamp; }
}