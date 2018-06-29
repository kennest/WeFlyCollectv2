package com.wefly.wecollect.models;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;

import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.ChipInterface;
import com.wefly.wecollect.utils.AppController;
import com.weflyagri.wecollect.R;

import java.io.File;

public class Piece implements ChipInterface{
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


    public String getExtension(String url) {
        String ext="";
               ext = url.substring(url.lastIndexOf("."));
        return ext;
    }


    //CHIPS INPUT METHODS
    @Override
    public Object getId() {
        return null;
    }

    @Override
    public Uri getAvatarUri() {
        return null;
    }

    @Override
    public Drawable getAvatarDrawable() {
        return ContextCompat.getDrawable(AppController.getInstance().getApplicationContext(), R.drawable.ic_attachement);
    }

    @Override
    public String getLabel() {
        return "PJ_"+System.nanoTime()+this.getExtension(this.getUrl());
    }

    @Override
    public String getInfo() {
        return null;
    }
}
