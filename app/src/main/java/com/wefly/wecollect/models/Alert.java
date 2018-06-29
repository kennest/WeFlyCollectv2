package com.wefly.wecollect.models;

import android.support.annotation.NonNull;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by admin on 07/06/2018.
 */
@Table(name = "alertes")
public class Alert extends Model implements Serializable {
    private static final long serialVersionUID = 10L;

    @Column(name = "id")
    private int alertId;

    @Column(name ="objet")
    private String object;

    @Column(name = "contenu")
    private String content;

    @Column(name = "categorie")
    private String category;

    @Column(name = "date_creation")
    private String dateCreated;


    private String stRecipients;

    @Column(name = "destinataires")
    private CopyOnWriteArrayList<Recipient> recipients = new CopyOnWriteArrayList<>();

    public String getDateCreated() {
        if (dateCreated == null)
            dateCreated = "";
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }


    public int getAlertId() {
        return alertId;
    }

    public void setAlertId(int alertId) {
        this.alertId = alertId;
    }

    public String getObject() {
        if (object == null)
            object = "";
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getContent() {
        if (content == null)
            content = "";
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        if (category == null)
            category = "";
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

}
