package com.wefly.wecollect.utils;

/**
 * Created by admin on 01/06/2018.
 */

public class Constants {

    // APP NAME
    public static final String APP_NAME = "WeFlyCollect";
    public static final String PATH = "WeFlyCollect";

    // BASE
    //public static final String BASE_URL = "http://192.168.1.66:8000/";
    public static final String BASE_URL = "http://217.182.133.143:8000/";

    // GET
    public static final String LOGIN_URL = BASE_URL + "login/";

    //POST
    public static final String SEND_SMS_URL = BASE_URL + "communications/api/sms/";

    //GET
    public static final String RECIPIENTS_URL = BASE_URL + "communications/api/liste-employes/";
    //GET
    public static final String SMS_RECEIVE_URL = BASE_URL + "sms-recu-mobile/?recu=";
    //GET
    public static final String SMS_SENT_URL = BASE_URL + "sms/";

    public static final String EMAIL_RECEIVE_URL = BASE_URL + "communications/api/email-receive-status/";

    public static final String EMAIL_SENT_URL = BASE_URL + "communications/api/email-display/";

    public static final String ALERT_RECEIVE_URL = BASE_URL + "communications/api/alerte-receive-status/";

    public static final String SEND_EMAIL_URL = BASE_URL + "communications/api/emails/";

    public static final String SEND_FILE_URL = BASE_URL + "communications/api/files/";

    public static final String SEND_ALERT_URL = BASE_URL + "communications/api/alertes/";

    public static final String ALERT_CATEGORY_URL = BASE_URL + "communications/api/categorie-alerte/";

    //Util
    public static final double DOUBLE_NULL = 0.0d;

    public static final String PREF_TOKEN = PATH + ".token";
    public static final String PREF_USER_NAME = PATH + ".user.name";
    public static final String PREF_USER_PASSWORD = PATH + ".user.password";

    //Preference
    public static final String SAVE_PREFERENCE_NAME = "NonameSave";
    public static final String PREF_RECIPIENTS = PATH + ".precipients";


    //Volley
    public static final int VOLLEY_TIME_OUT = 300000; //5 min
    public static final int CLOSE_DELAY = 1200;

    //JWT TOKEN
    public static final String TOKEN_HEADER_NAME = "JWT ";

    //NetWork
    public static final String SERVER_ERROR = "error";
    public static final String RESPONSE_EMPTY_INPUT = "non_field_errors";
    public static final String RESPONSE_ERROR_SMS = "{}";
    public static final String RESPONSE_ERROR_SMS_NOT_SENT = "{\"contenu\":[]}";
    public static final String RESPONSE_EMPTY = "{}";
    public static final String RESPONSE_ERROR_HTML = "<html>";
    // Drawer item size
    public static final int DRAWER_ICON_SIZE = 24;
    public static final int DRAWER_ICON_BIG_SIZE = 32;
    public static final int DRAWER_ICON_PADDING = 4;
    public static final int HAMBURGER_ICON_SIZE = 24;

    // Save state
    public static final String STATE_USER_NAME = PATH + ".state.user.name";
    public static final String STATE_USER_PASSWORD = PATH + ".state.user.password";
    public static final String STATE_SMS = PATH + ".state.sms";
    public static final String STATE_EMAIL = PATH + ".state.email";
    public static final String STATE_ALERT = PATH + ".state.alert";

    // Parent
    public static final int BELONG_TO_SMS = 1;
    public static final int BELONG_TO_EMAIL = 2;
    public static final int BELONG_TO_ALERT = 3;


    //Permission
    public static final int PERMISSIONS_REQUEST = 100;
    public static final int ENABLE_LOCATION_SERVICES_REQUEST = 101;
    public static final int GOOGLE_PLAY_SERVICES_ERROR_DIALOG = 102;
    public static final int REQUEST_READ_SMS_PERMISSION = 3004;
    public static final int REQUEST_GROUP_PERMISSION = 425;
    public static final int REQUEST_APP_PERMISSION = 425;
    public static final int REQUEST_CODE_SELECT_IMAGES = 100;

    //DB
    public static final String DATABASE_NAME = "wecollectdb";
    public static final int DATABASE_VERSION = 1;

    //Gallery
    public static final int MAX_SELECT_COUNT = 50;


    //DB Table Email
    public static final String TABLE_EMAIL = "email_tbl";
    public static final String TABLE_EMAIL_KEY_ID = "_id";
    public static final String TABLE_EMAIL_OBJECT_NAME = "_obj";
    public static final String TABLE_EMAIL_CONTENT_NAME = "_content";
    public static final String TABLE_EMAIL_ATTACHMENT_PATH_NAME = "_attachment";
    public static final String TABLE_EMAIL_CREATED_DATE_NAME = "_date_em";
    public static final String TABLE_EMAIL_RECIPIENTS_ID_NAME = "_recip";
    public static final String TABLE_EMAIL_SENDER_NAME = "_sender";

    //DB Table ALERT
    public static final String TABLE_ALERT = "alert_tbl";
    public static final String TABLE_ALERT_KEY_ID = "_id";
    public static final String TABLE_ALERT_OBJECT_NAME = "_obj";
    public static final String TABLE_ALERT_CONTENT_NAME = "_content";
    public static final String TABLE_ALERT_CATEGORY_NAME = "_category";
    public static final String TABLE_ALERT_CREATED_DATE_NAME = "_date_al";
    public static final String TABLE_ALERT_RECIPIENTS_ID_NAME = "_recip";
    public static final String TABLE_ALERT_SENDER_NAME = "_sender";

    //DB Table Sms
    public static final String TABLE_SMS = "sms_tbl";
    public static final String TABLE_SMS_KEY_ID = "_id";
    public static final String TABLE_SMS_CONTENT_NAME = "_content";
    public static final String TABLE_SMS_ID_ON_SERVER_NAME = "_id_on_server";
    public static final String TABLE_SMS_AUTHOR_ID_NAME = "_author_id";
    public static final String TABLE_SMS_IS_READ_NAME = "_is_read";
    public static final String TABLE_SMS_CREATED_DATE_NAME = "_date_sms";
    public static final String TABLE_SMS_RECIPIENTS_ID_NAME = "_recip";
    public static final String TABLE_SMS_SENDER_NAME = "_sender";

    //DB Table Recipient
    public static final String TABLE_RECIPIENT = "recip_tbl";
    public static final String TABLE_RECIPIENT_KEY_ID = "_id";
    public static final String TABLE_RECIPIENT_ID_ON_SERVER_NAME = "_id_on_serv";
    public static final String TABLE_RECIPIENT_FISRT_NAME_NAME = "_first_name";
    public static final String TABLE_RECIPIENT_LAST_NAME_NAME = "_last_name";
    public static final String TABLE_RECIPIENT_USER_NAME_NAME = "_user_name";
    public static final String TABLE_RECIPIENT_EMAIL_NAME = "_email";
    public static final String TABLE_RECIPIENT_TELEPHONE_NAME = "_tel";
    public static final String TABLE_RECIPIENT_REFERENCE_NAME = "_ref";
    public static final String TABLE_RECIPIENT_CREATE_DATE_NAME_ = "_date_recip";
    public static final String TABLE_RECIPIENT_IS_DELETE_NAME = "_delete";
    public static final String TABLE_RECIPIENT_FONCTION_NAME = "_fct";
    public static final String TABLE_RECIPIENT_ADRESSE_NAME = "_adresse";
    public static final String TABLE_RECIPIENT_ROLE_NAME = "_role";
    public static final String TABLE_RECIPIENT_ENTREPRISE_NAME = "_entre";
    public static final String TABLE_RECIPIENT_BELONG_TO_NAME = "_belon_to";
    public static final String TABLE_RECIPIENT_BELONG_ID_NAME = "belong_id";

    //DB Table common
    public static final String TABLE_COMMON = "common_tbl";
    public static final String TABLE_COMMON_KEY_ID = "_id";
    public static final String TABLE_COMMON_HAS_NEXT_NAME = "_has_next";
    public static final String TABLE_COMMON_HAS_PREVIOUS_NAME = "_has_prev";
    public static final String TABLE_COMMON_NEXT_PAGE_NAME = "_next_pg";
    public static final String TABLE_COMMON_PREVIOUS_PAGE_NAME = "_prev_pg";
    public static final String TABLE_COMMON_COUNT_NAME = "_count";

}
