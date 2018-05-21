package com.indracompany.sofia2.android.healthcheckapp;

/**
 * Created by mbriceno on 18/05/2018.
 */

public class RequestData {
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPending() {
        return pending;
    }

    public void setPending(String pending) {
        this.pending = pending;
    }

    private String username;
    private String pending;


}
