package com.wefly.wealert.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.pchmn.materialchips.model.ChipInterface;

import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by admin on 13/06/2018.
 */
@Table(name = "destinataires")
public class Recipient extends Model implements Serializable, ChipInterface {
    private static final long serialVersionUID = 10L;

    @Column(name = "id")
    private int recipientId;


    private int idOnServer;

    @Column(name = "nom")
    private String firstName;

    @Column(name = "prenom")
    private String lastName;

    @Column(name = "username")
    private String userName;

    @Column(name = "email")
    private String email;

    private String avatarUrl;

    private String tel;
    private String ref;
    private String dateCreate;
    private boolean deleted;
    private int fonction;
    private int adresse;
    private int role;
    private int entreprise;
    private int superieur;
    private int belongTo;
    private int belongId;


    public int getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }

    public int getIdOnServer() {
        return idOnServer;
    }

    public void setIdOnServer(int idOnServer) {
        this.idOnServer = idOnServer;
    }

    public String getFirstName() {
        if (firstName == null)
            firstName = "";
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        if (lastName == null)
            lastName = "";
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        if (userName == null)
            userName = "";
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        if (email == null)
            email = "";
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        if (tel == null)
            tel = "";
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getRef() {
        if (ref == null)
            ref = "";
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getDateCreate() {
        if (dateCreate == null)
            dateCreate = "";
        return dateCreate;
    }

    public void setDateCreate(String dateCreate) {
        this.dateCreate = dateCreate;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getFonction() {
        return fonction;
    }

    public void setFonction(int fonction) {
        this.fonction = fonction;
    }

    public int getAdresse() {
        return adresse;
    }

    public void setAdresse(int adresse) {
        this.adresse = adresse;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getEntreprise() {
        return entreprise;
    }

    public void setEntreprise(int entreprise) {
        this.entreprise = entreprise;
    }

    public int getSuperieur() {
        return superieur;
    }

    public void setSuperieur(int superieur) {
        this.superieur = superieur;
    }

    public int getBelongTo() {
        return belongTo;
    }

    public void setBelongTo(int belongTo) {
        this.belongTo = belongTo;
    }

    public int getBelongId() {
        return belongId;
    }

    public void setBelongId(int belongId) {
        this.belongId = belongId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }


    @Override
    public Uri getAvatarUri() {
        return null;
    }

    @Override
    public Drawable getAvatarDrawable() {
        return null;
    }

    @Override
    public String getLabel() {
        return getUserName();
    }

    @Override
    public String getInfo() {
        return null;
    }
}
