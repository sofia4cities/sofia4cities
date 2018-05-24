package com.indracompany.sofia2.android.healthcheckapp;

/**
 * Created by mbriceno on 18/05/2018.
 */

public class CitiInboxData {


    public String getSpecialist() {
        return specialist;
    }

    public void setSpecialist(String specialist) {
        this.specialist = specialist;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


    private String specialist;
    private String feedback;
    private String timestamp;


}
