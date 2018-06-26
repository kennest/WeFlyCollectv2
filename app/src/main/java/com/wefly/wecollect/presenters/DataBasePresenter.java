package com.wefly.wecollect.presenters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.wefly.wecollect.models.Alert;
import com.wefly.wecollect.models.Common;
import com.wefly.wecollect.models.Email;
import com.wefly.wecollect.models.Recipient;
import com.wefly.wecollect.models.Sms;
import com.wefly.wecollect.utils.Constants;
import com.wefly.wecollect.utils.Save;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by admin on 27/03/2018.
 */

public class DataBasePresenter extends SQLiteOpenHelper {


    private static DataBasePresenter instance;
    private final CopyOnWriteArrayList<Email> emailList = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Alert> alertList = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Common> commonList = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Sms> smsList = new CopyOnWriteArrayList<>();
    private final String TAG = getClass().getSimpleName();

    public DataBasePresenter(final Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);

    }

    public static void init(@NonNull Context ctx) {
        if (null == instance) {
            instance = new DataBasePresenter(ctx);
        }
    }

    public static @NonNull
    DataBasePresenter getInstance() {
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            //create table EMAIL
            String CREATE_TABLE_EMAIL = "CREATE TABLE " + Constants.TABLE_EMAIL + "("
                    + Constants.TABLE_EMAIL_KEY_ID + " INTEGER PRIMARY KEY, "
                    + Constants.TABLE_EMAIL_OBJECT_NAME + " TEXT, "
                    + Constants.TABLE_EMAIL_CONTENT_NAME + " TEXT, "
                    + Constants.TABLE_EMAIL_ATTACHMENT_PATH_NAME + " TEXT, "
                    + Constants.TABLE_EMAIL_SENDER_NAME + " TEXT, "
                    + Constants.TABLE_EMAIL_RECIPIENTS_ID_NAME + " TEXT, "
                    + Constants.TABLE_EMAIL_CREATED_DATE_NAME + " TEXT);";


            //create table ALERT
            String CREATE_TABLE_ALERT = "CREATE TABLE " + Constants.TABLE_ALERT + "("
                    + Constants.TABLE_ALERT_KEY_ID + " INTEGER PRIMARY KEY, "
                    + Constants.TABLE_ALERT_OBJECT_NAME + " TEXT, "
                    + Constants.TABLE_ALERT_CONTENT_NAME + " TEXT, "
                    + Constants.TABLE_ALERT_CATEGORY_NAME + " TEXT, "
                    + Constants.TABLE_ALERT_SENDER_NAME + " TEXT, "
                    + Constants.TABLE_ALERT_RECIPIENTS_ID_NAME + " TEXT, "
                    + Constants.TABLE_ALERT_CREATED_DATE_NAME + " TEXT);";


            //create table Sms
            String CREATE_TABLE_SMS = "CREATE TABLE " + Constants.TABLE_SMS + "("
                    + Constants.TABLE_SMS_KEY_ID + " INTEGER PRIMARY KEY, "
                    + Constants.TABLE_SMS_CONTENT_NAME + " TEXT, "
                    + Constants.TABLE_SMS_SENDER_NAME + " TEXT, "
                    + Constants.TABLE_SMS_RECIPIENTS_ID_NAME + " TEXT, "
                    + Constants.TABLE_SMS_ID_ON_SERVER_NAME + " INT, "
                    + Constants.TABLE_SMS_AUTHOR_ID_NAME + " INT, "
                    + Constants.TABLE_SMS_IS_READ_NAME + " INT, "
                    + Constants.TABLE_SMS_CREATED_DATE_NAME + " TEXT);";

            //create table Sms
            String CREATE_TABLE_COMMON = "CREATE TABLE " + Constants.TABLE_COMMON + "("
                    + Constants.TABLE_COMMON_KEY_ID + " INTEGER PRIMARY KEY, "
                    + Constants.TABLE_COMMON_HAS_NEXT_NAME + " INT, "
                    + Constants.TABLE_COMMON_HAS_PREVIOUS_NAME + " INT, "
                    + Constants.TABLE_COMMON_NEXT_PAGE_NAME + " TEXT, "
                    + Constants.TABLE_COMMON_PREVIOUS_PAGE_NAME + " TEXT, "
                    + Constants.TABLE_COMMON_COUNT_NAME + " INT);";


            db.execSQL(CREATE_TABLE_EMAIL);
            db.execSQL(CREATE_TABLE_ALERT);
            db.execSQL(CREATE_TABLE_SMS);
            db.execSQL(CREATE_TABLE_COMMON);
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(Constants.APP_NAME, TAG + "onCreate fail");
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_EMAIL);
            db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_ALERT);
            db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_SMS);
            db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_COMMON);

            //create a new one
            onCreate(db);
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(Constants.APP_NAME, TAG + "onUpgrade fail");
        }

    }


    //Get total items saved
    public int getEmailTotalItems() {
        int totalItems = 0;
        try {
            String query = "SELECT * FROM " + Constants.TABLE_EMAIL;
            SQLiteDatabase dba = this.getReadableDatabase();
            Cursor cursor = dba.rawQuery(query, null);

            totalItems = cursor.getCount();

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(Constants.APP_NAME, TAG + "getParcelleTotalItems error");
        }

        return totalItems;

    }


    public int getAlertTotalItems() {
        int totalItems = 0;
        try {
            String query = "SELECT * FROM " + Constants.TABLE_ALERT;
            SQLiteDatabase dba = this.getReadableDatabase();
            Cursor cursor = dba.rawQuery(query, null);

            totalItems = cursor.getCount();

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(Constants.APP_NAME, "TAG getParcelleTotalItems error");
        }

        return totalItems;

    }

    public int getSmsTotalItems() {
        int totalItems = 0;
        try {
            String query = "SELECT * FROM " + Constants.TABLE_SMS;
            SQLiteDatabase dba = this.getReadableDatabase();
            Cursor cursor = dba.rawQuery(query, null);

            totalItems = cursor.getCount();

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(Constants.APP_NAME, TAG + "getParcelleTotalItems error");
        }

        return totalItems;

    }

    //delete email item
    public boolean deleteEmail(int emailId) {
        try {
            SQLiteDatabase dba = this.getWritableDatabase();
            dba.delete(Constants.TABLE_EMAIL, Constants.TABLE_EMAIL_KEY_ID + " = ?",
                    new String[]{String.valueOf(emailId)});

            dba.close();


            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(Constants.APP_NAME, TAG + "deleteEmail Id " + emailId + "fail");
            return false;
        }

    }


    //delete email item
    public boolean deleteAlert(int alertId) {
        try {
            SQLiteDatabase dba = this.getWritableDatabase();
            dba.delete(Constants.TABLE_ALERT, Constants.TABLE_ALERT_KEY_ID + " = ?",
                    new String[]{String.valueOf(alertId)});

            dba.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(Constants.APP_NAME, TAG + "deleteAlert  Id " + alertId + "fail");
            return false;
        }

    }

    //delete Sms item
    public boolean deleteSms(int smsId) {
        try {
            SQLiteDatabase dba = this.getWritableDatabase();
            dba.delete(Constants.TABLE_SMS, Constants.TABLE_SMS_KEY_ID + " = ?",
                    new String[]{String.valueOf(smsId)});

            dba.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(Constants.APP_NAME, TAG + "deleteSms  Id " + smsId + "fail");
            return false;
        }

    }

    //delete Sms item
    public boolean deleteCommon(int commonId) {
        try {
            SQLiteDatabase dba = this.getWritableDatabase();
            dba.delete(Constants.TABLE_COMMON, Constants.TABLE_COMMON_KEY_ID + " = ?",
                    new String[]{String.valueOf(commonId)});

            dba.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(Constants.APP_NAME, TAG + "deletecommon  Id " + commonId + "fail");
            return false;
        }

    }


    //add content to db - add Email
    public boolean addEmail(@NonNull Email email) {
        try {
            SQLiteDatabase dba = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(Constants.TABLE_EMAIL_OBJECT_NAME, email.getObject());
            values.put(Constants.TABLE_EMAIL_CONTENT_NAME, email.getContent());
            values.put(Constants.TABLE_EMAIL_ATTACHMENT_PATH_NAME, email.getAttachment());
            values.put(Constants.TABLE_EMAIL_CREATED_DATE_NAME, email.getDateCreated());
            values.put(Constants.TABLE_EMAIL_RECIPIENTS_ID_NAME, email.getRecipientsIds());
            values.put(Constants.TABLE_EMAIL_SENDER_NAME, email.getSender());


            dba.insert(Constants.TABLE_EMAIL, null, values);


            dba.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(Constants.APP_NAME, TAG + "addEmail Error");
        }

        return false;
    }

    //add content to db - add Alert
    public boolean addAlert(@NonNull Alert alert) {
        try {
            SQLiteDatabase dba = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(Constants.TABLE_ALERT_OBJECT_NAME, alert.getObject());
            values.put(Constants.TABLE_ALERT_CONTENT_NAME, alert.getContent());
            values.put(Constants.TABLE_ALERT_CATEGORY_NAME, alert.getCategory());
            values.put(Constants.TABLE_ALERT_CREATED_DATE_NAME, alert.getDateCreated());
            values.put(Constants.TABLE_ALERT_RECIPIENTS_ID_NAME, alert.getRecipientsIds());
            values.put(Constants.TABLE_ALERT_SENDER_NAME, alert.getSender());

            dba.insert(Constants.TABLE_ALERT, null, values);


            dba.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(Constants.APP_NAME, TAG + "addAlert Error");
        }

        return false;
    }


    //add content to db - add Sms
    public boolean addSms(@NonNull Sms sms) {
        try {
            SQLiteDatabase dba = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(Constants.TABLE_SMS_CONTENT_NAME, sms.getContent());
            values.put(Constants.TABLE_SMS_CREATED_DATE_NAME, sms.getDateCreated());
            values.put(Constants.TABLE_SMS_SENDER_NAME, sms.getSender());
            values.put(Constants.TABLE_SMS_ID_ON_SERVER_NAME, sms.getIdOnServer());
            values.put(Constants.TABLE_SMS_AUTHOR_ID_NAME, sms.getAuthorId());
            values.put(Constants.TABLE_SMS_IS_READ_NAME, sms.getIsReadAsInt());
            values.put(Constants.TABLE_SMS_RECIPIENTS_ID_NAME, sms.getRecipientsIds());

            dba.insert(Constants.TABLE_SMS, null, values);


            dba.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(Constants.APP_NAME, TAG + "addSMS Error");
        }

        return false;
    }


    //add content to db - add common
    public boolean addCommon(@NonNull Common com) {
        try {
            SQLiteDatabase dba = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(Constants.TABLE_COMMON_HAS_NEXT_NAME, com.getHasNextAsInt());
            values.put(Constants.TABLE_COMMON_HAS_PREVIOUS_NAME, com.getHasPreviousAsInt());
            values.put(Constants.TABLE_COMMON_NEXT_PAGE_NAME, com.getNextPage());
            values.put(Constants.TABLE_COMMON_PREVIOUS_PAGE_NAME, com.getPrevPage());
            values.put(Constants.TABLE_COMMON_COUNT_NAME, com.getCount());

            dba.insert(Constants.TABLE_COMMON, null, values);


            dba.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(Constants.APP_NAME, TAG + "addCommon Error");
        }

        return false;
    }

//    private void updatePoint(@NonNull Point point){
//        try {
//            SQLiteDatabase dba = this.getWritableDatabase();
//            String updateId = Integer.toString(point.getPointId());
//            ContentValues values = new ContentValues();
//
//
//            values.put(Constants.TABLE_POINT_PARCELLE_ID_NAME, point.getParcelleId());
//            values.put(Constants.TABLE_POINT_RANG_NAME, point.getRang());
//            values.put(Constants.TABLE_POINT_LATITUDE_NAME, point.getLatitude());
//            values.put(Constants.TABLE_POINT_LONGITUDE_NAME, point.getLongitude());
//            values.put(Constants.TABLE_POINT_IS_REFERNCE_NAME, point.getIsReferenceAsInt());
//            values.put(Constants.TABLE_POINT_IS_CENTER_NAME, point.getIsCenterAsInt());
//
//            dba.update(Constants.TABLE_POINT, values, Constants.TABLE_POINT_KEY + "=?", new String[]{updateId});
//            dba.close();
//        }catch (Exception e){
//            e.printStackTrace();
//            Log.v(Constants.APP_NAME, TAG + "updatePoint Error");
//        }
//    }


    //Get all Email
    public CopyOnWriteArrayList<Email> getEmails() {

        emailList.clear();
        try {


            SQLiteDatabase dba = this.getReadableDatabase();
            Cursor cursor = dba.query(Constants.TABLE_EMAIL,
                    new String[]{
                            Constants.TABLE_EMAIL_KEY_ID,
                            Constants.TABLE_EMAIL_OBJECT_NAME,
                            Constants.TABLE_EMAIL_CONTENT_NAME,
                            Constants.TABLE_EMAIL_ATTACHMENT_PATH_NAME,
                            Constants.TABLE_EMAIL_SENDER_NAME,
                            Constants.TABLE_EMAIL_RECIPIENTS_ID_NAME,
                            Constants.TABLE_EMAIL_CREATED_DATE_NAME}, null, null, null, null, null);

            //loop through...
            if (cursor.moveToFirst()) {
                do {

                    Email email = new Email();

                    email.setEmailId(cursor.getInt(cursor.getColumnIndex(Constants.TABLE_EMAIL_KEY_ID)));
                    email.setObject(cursor.getString(cursor.getColumnIndex(Constants.TABLE_EMAIL_OBJECT_NAME)));
                    email.setContent(cursor.getString(cursor.getColumnIndex(Constants.TABLE_EMAIL_CONTENT_NAME)));
                    email.setAttachment(cursor.getString(cursor.getColumnIndex(Constants.TABLE_EMAIL_ATTACHMENT_PATH_NAME)));
                    email.setSender(cursor.getString(cursor.getColumnIndex(Constants.TABLE_EMAIL_SENDER_NAME)));
                    email.setRecipientsString(cursor.getString(cursor.getColumnIndex(Constants.TABLE_EMAIL_RECIPIENTS_ID_NAME)));
                    email.setDateCreated(cursor.getString(cursor.getColumnIndex(Constants.TABLE_EMAIL_CREATED_DATE_NAME)));

                    emailList.add(email);

                } while (cursor.moveToNext());


            }

            cursor.close();
            dba.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(Constants.APP_NAME, TAG + "getPoints Error");
        }

        return emailList;

    }

    //Get all Alert
    public CopyOnWriteArrayList<Alert> getAlerts() {

        alertList.clear();
        try {


            SQLiteDatabase dba = this.getReadableDatabase();
            Cursor cursor = dba.query(Constants.TABLE_ALERT,
                    new String[]{
                            Constants.TABLE_ALERT_KEY_ID,
                            Constants.TABLE_ALERT_OBJECT_NAME,
                            Constants.TABLE_ALERT_CONTENT_NAME,
                            Constants.TABLE_ALERT_CATEGORY_NAME,
                            Constants.TABLE_ALERT_SENDER_NAME,
                            Constants.TABLE_ALERT_RECIPIENTS_ID_NAME,
                            Constants.TABLE_ALERT_CREATED_DATE_NAME}, null, null, null, null, null);

            //loop through...
            if (cursor.moveToFirst()) {
                do {

                    Alert alert = new Alert();

                    alert.setAlertId(cursor.getInt(cursor.getColumnIndex(Constants.TABLE_ALERT_KEY_ID)));
                    alert.setObject(cursor.getString(cursor.getColumnIndex(Constants.TABLE_ALERT_OBJECT_NAME)));
                    alert.setContent(cursor.getString(cursor.getColumnIndex(Constants.TABLE_ALERT_CONTENT_NAME)));
                    alert.setCategory(cursor.getString(cursor.getColumnIndex(Constants.TABLE_ALERT_CATEGORY_NAME)));
                    alert.setSender(cursor.getString(cursor.getColumnIndex(Constants.TABLE_ALERT_SENDER_NAME)));
                    alert.setRecipientsString(cursor.getString(cursor.getColumnIndex(Constants.TABLE_ALERT_RECIPIENTS_ID_NAME)));
                    alert.setDateCreated(cursor.getString(cursor.getColumnIndex(Constants.TABLE_ALERT_CREATED_DATE_NAME)));


                    alertList.add(alert);

                } while (cursor.moveToNext());


            }

            cursor.close();
            dba.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(Constants.APP_NAME, TAG + "getAlerts Error");
        }

        return alertList;

    }


    //Get all Sms
    public CopyOnWriteArrayList<Sms> getSms() {

        smsList.clear();
        try {

            SQLiteDatabase dba = this.getReadableDatabase();
            Cursor cursor = dba.query(Constants.TABLE_SMS,
                    new String[]{
                            Constants.TABLE_SMS_KEY_ID,
                            Constants.TABLE_SMS_CONTENT_NAME,
                            Constants.TABLE_SMS_SENDER_NAME,
                            Constants.TABLE_SMS_ID_ON_SERVER_NAME,
                            Constants.TABLE_SMS_AUTHOR_ID_NAME,
                            Constants.TABLE_SMS_IS_READ_NAME,
                            Constants.TABLE_SMS_RECIPIENTS_ID_NAME,
                            Constants.TABLE_SMS_CREATED_DATE_NAME}, null, null, null, null, null);

            //loop through...
            if (cursor.moveToFirst()) {
                do {

                    Sms sms = new Sms();

                    sms.setSmsId(cursor.getInt(cursor.getColumnIndex(Constants.TABLE_SMS_KEY_ID)));
                    sms.setContent(cursor.getString(cursor.getColumnIndex(Constants.TABLE_SMS_CONTENT_NAME)));
                    sms.setSender(cursor.getString(cursor.getColumnIndex(Constants.TABLE_SMS_SENDER_NAME)));
                    sms.setRecipientsString(cursor.getString(cursor.getColumnIndex(Constants.TABLE_SMS_RECIPIENTS_ID_NAME)));
                    sms.setIdOnServer(cursor.getInt(cursor.getColumnIndex(Constants.TABLE_SMS_ID_ON_SERVER_NAME)));
                    sms.setAuthorId(cursor.getInt(cursor.getColumnIndex(Constants.TABLE_SMS_AUTHOR_ID_NAME)));
                    sms.setRead(cursor.getInt(cursor.getColumnIndex(Constants.TABLE_SMS_IS_READ_NAME)));
                    sms.setDateCreated(cursor.getString(cursor.getColumnIndex(Constants.TABLE_SMS_CREATED_DATE_NAME)));

                    smsList.add(sms);

                } while (cursor.moveToNext());


            }

            cursor.close();
            dba.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(Constants.APP_NAME, TAG + "getPoints Error");
        }

        return smsList;

    }

    // Get common
    public CopyOnWriteArrayList<Common> getCommon() {

        commonList.clear();
        try {

            SQLiteDatabase dba = this.getReadableDatabase();
            Cursor cursor = dba.query(Constants.TABLE_COMMON,
                    new String[]{
                            Constants.TABLE_COMMON_KEY_ID,
                            Constants.TABLE_COMMON_HAS_NEXT_NAME,
                            Constants.TABLE_COMMON_HAS_PREVIOUS_NAME,
                            Constants.TABLE_COMMON_NEXT_PAGE_NAME,
                            Constants.TABLE_COMMON_PREVIOUS_PAGE_NAME,
                            Constants.TABLE_COMMON_COUNT_NAME}, null, null, null, null, null);

            //loop through...
            if (cursor.moveToFirst()) {
                do {

                    Sms sms = new Sms();

                    sms.setCommonId(cursor.getInt(cursor.getColumnIndex(Constants.TABLE_COMMON_KEY_ID)));
                    sms.setHasNext(cursor.getInt(cursor.getColumnIndex(Constants.TABLE_COMMON_HAS_NEXT_NAME)));
                    sms.setHasPrevious(cursor.getInt(cursor.getColumnIndex(Constants.TABLE_COMMON_HAS_PREVIOUS_NAME)));
                    sms.setNextPage(cursor.getString(cursor.getColumnIndex(Constants.TABLE_COMMON_NEXT_PAGE_NAME)));
                    sms.setPrevPage(cursor.getString(cursor.getColumnIndex(Constants.TABLE_COMMON_PREVIOUS_PAGE_NAME)));
                    sms.setCount(cursor.getInt(cursor.getColumnIndex(Constants.TABLE_COMMON_COUNT_NAME)));

                    commonList.add(sms);

                } while (cursor.moveToNext());


            }

            cursor.close();
            dba.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(Constants.APP_NAME, TAG + "getPoints Error");
        }

        return commonList;

    }

    public void saveRecipientList(@NonNull Context ctx, @NonNull JSONArray array) {
        try {
            Save.defaultSaveString(Constants.PREF_RECIPIENTS, array.toString(), ctx);
            Log.v(Constants.APP_NAME, TAG + " saveRecipientsList SUCCES STORE IN SHARED " + array.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(Constants.APP_NAME, TAG + "saveRecipientsList Error");
        }
    }

    public void clearRecipients(@NonNull Context ctx) {
        try {
            Save.defaultSaveString(Constants.PREF_RECIPIENTS, "", ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public @NonNull
    CopyOnWriteArrayList<Recipient> getRecipients(@NonNull Context ctx) {
        CopyOnWriteArrayList<Recipient> list = new CopyOnWriteArrayList<>();

        try {
            String res = Save.defaultLoadString(Constants.PREF_RECIPIENTS, ctx);
            Log.v(Constants.APP_NAME, TAG + " SharedPref res JSONArray" + res);
            JSONArray array = new JSONArray(res);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Recipient reci = new Recipient();
                reci.setIdOnServer(obj.getInt("id"));
                reci.setTel(obj.getString("telephone"));
                reci.setRef(obj.getString("reference"));
                reci.setDateCreate(obj.getString("create_at"));
                reci.setDeleted(obj.getBoolean("delete"));
                reci.setFonction(obj.getInt("fonction"));
                reci.setAdresse(obj.getInt("adresse"));
                reci.setRole(obj.getInt("role"));
                reci.setEntreprise(obj.getInt("entreprise"));
                reci.setSuperieur(obj.getInt("superieur"));
                reci.setFirstName(obj.getString("first_name"));
                reci.setLastName(obj.getString("last_name"));
                reci.setEmail(obj.getString("email"));
                reci.setUserName(obj.getString("username"));

                list.add(reci);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(Constants.APP_NAME, TAG + " CAN'T Recipients");
        }

        return list;
    }


    public void onInit() {
        emailList.clear();
        alertList.clear();
        smsList.clear();
        // reload everything in all Array
    }

    public void onReload() {
        onInit();
    }
}
