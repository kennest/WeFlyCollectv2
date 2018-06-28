package com.wefly.wecollect.models;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by admin on 07/06/2018.
 */

public class Email extends Common implements Serializable {
    private static final long serialVersionUID = 10L;
    private int emailId;
    private String content;
    private String object;
    private String attachment;
    private String dateCreated;
    private String stRecipients;
    private String expediteur;
    private CopyOnWriteArrayList<Recipient> recipients = new CopyOnWriteArrayList<>();

    public Email(String object, String dateCreated,String content) {
        this.object = object;
        this.dateCreated = dateCreated;
        this.recipients = recipients;
        this.content=content;
    }

    public String getExpediteur() {
        return expediteur;
    }

    public void setExpediteur(String expediteur) {
        this.expediteur = expediteur;
    }

    public Email() {
    }

    public String getDateCreated() {
        if (dateCreated == null)
            dateCreated = "";
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }


    public int getEmailId() {
        return emailId;
    }

    public void setEmailId(int emailId) {
        this.emailId = emailId;
    }

    public String getContent() {
        if (content == null)
            content = "";
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getObject() {
        if (object == null)
            object = "";
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getAttachment() {
        if (attachment == null)
            attachment = "";
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getRecipientsIds() {
        CopyOnWriteArrayList<Recipient> list = new CopyOnWriteArrayList<>();
        list.addAll(getRecipients());
        String ids = "[";

        if (list.size() > 0) {

            // 1 ... n-1
            for (int i = 0; i < list.size() - 1; i++) {
                ids += list.get(i).getIdOnServer() + ",";
            }
            // n
            ids += list.get(list.size() - 1).getIdOnServer();
        }

        ids += "]";

        return ids;
    }

    public CopyOnWriteArrayList<Recipient> getRecipients() {
        return recipients;
    }

    public void setRecipients(@NonNull CopyOnWriteArrayList<Recipient> recipients) {
        this.recipients.clear();
        this.recipients.addAll(recipients);
    }

    public String getRecipientsString() {
        if (stRecipients == null)
            stRecipients = "";
        return stRecipients;
    }

    public void setRecipientsString(String recipientsString) {
        this.stRecipients = recipientsString;
    }

    public JSONObject getJSON() {
        JSONObject jsonEmail = new JSONObject();
        try {
            jsonEmail.put("contenu", this.content);
            //jsonEmail.put("attachement",this.attachment);
            jsonEmail.put("objet", this.object);
            jsonEmail.put("destinataires", getRecipientsIds());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonEmail;
    }
}
