package com.wefly.wecollect.model;

import android.net.Uri;

import java.io.File;
import java.util.Base64;

public class Piece {
    private String url;
    private Uri contentUrl;
    private Integer email;
    private Integer alert;

    public Uri getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(Uri contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getEmail() {
        return email;
    }

    public void setEmail(Integer email) {
        this.email = email;
    }

    public Integer getAlert() {
        return alert;
    }

    public void setAlert(Integer alert) {
        this.alert = alert;
    }

    public File getFile() {
        return new File(this.getUrl());
    }

    //Get Image Base64 String
    public String base64() {
        File file=this.getFile();
        String base64String="";

        return base64String;
    }
}
