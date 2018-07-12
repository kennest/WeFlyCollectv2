package com.wefly.wealert.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by admin on 07/06/2018.
 */

public class Sms extends Common implements Serializable {
    private static final long serialVersionUID = 10L;
    private int smsId;
    private int idOnServer;
    private int authorId;
    private String content;
    private CopyOnWriteArrayList<Recipient> recipients = new CopyOnWriteArrayList<>();
    private String dateCreated;
    private boolean read;
    private String stRecipients;

    public String getDateCreated() {
        if (dateCreated == null)
            dateCreated = "";
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }


    public int getSmsId() {
        return smsId;
    }

    public void setSmsId(int smsId) {
        this.smsId = smsId;
    }


    public String getContent() {
        if (content == null)
            content = "";
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CopyOnWriteArrayList<Recipient> getRecipients() {
        return recipients;
    }

    public void setRecipients(@NonNull CopyOnWriteArrayList<Recipient> recipients) {
        this.recipients.clear();
        this.recipients.addAll(recipients);
    }

    public @Nullable
    JSONObject toPostItem() {
        JSONObject obj = null;
        String stItem = "";

        stItem += "{" +
                "\"contenu\":\"" + getContent() + "\", ";
        stItem += "\"destinataires\":" + getRecipientsIds();

        stItem += "}";

        try {
            obj = new JSONObject(stItem);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj;

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


    public int getIdOnServer() {
        return idOnServer;
    }

    public void setIdOnServer(int idOnServer) {
        this.idOnServer = idOnServer;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(int haNext) {
        read = haNext == 1;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public int getIsReadAsInt() {
        int bool = (read) ? 1 : 0;
        return bool;
    }

    public String getRecipientsString() {
        if (stRecipients == null)
            stRecipients = "";
        return stRecipients;
    }

    public void setRecipientsString(String recipientsString) {
        this.stRecipients = recipientsString;
    }
}
