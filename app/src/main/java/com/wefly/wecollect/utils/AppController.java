package com.wefly.wecollect.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.activeandroid.ActiveAndroid;
import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.wefly.wecollect.models.Piece;
import com.wefly.wecollect.models.Recipient;
import com.wefly.wecollect.tasks.RecipientGetTask;
import com.weflyagri.wecollect.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;


/**
 * Created by admin on 01/06/2018.
 */

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    private static AppController mInstance;
    private static ArrayList<Activity> activitiesList = new ArrayList<>();
    private static ArrayList<AsyncTask<Void, Integer, Boolean>> tasksList = new ArrayList<>();
    private String token = "";
    private static List<Piece> pieceList = new ArrayList<>();
    private static String audioPath;
    public Map<String, Integer> alert_categories = new HashMap();
    private CopyOnWriteArrayList<Recipient> recipientsList = new CopyOnWriteArrayList<>();
    List<Recipient> recipients= new ArrayList<>();
    public Double latitude;
    public Double longitude;

    public List<Recipient> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<Recipient> recipients) {
        this.recipients = recipients;
    }

    public CopyOnWriteArrayList<Recipient> getRecipientsList() {
        return recipientsList;
    }

    public void setRecipientsList(CopyOnWriteArrayList<Recipient> recipientsList) {
        this.recipientsList = recipientsList;
    }

    public List<Piece> getPieceList() {
        return pieceList;
    }

    public void setPieceList(List<Piece> pList) {
        this.pieceList=pList;
    }

    public static String getAudioPath() {
        return audioPath;
    }

    public static void setAudioPath(String audioPath) {
        AppController.audioPath = audioPath;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
        mInstance = this;
        try {
            recipients=new RecipientGetTask().execute().get();
        } catch (ExecutionException|InterruptedException e) {
            e.printStackTrace();
        }
        // Support vector
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        //FIXE CAMERA Bug on Api 24+
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    public static void addToDestroyList(final Activity activity) {
        try {
            activitiesList.add(activity);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    //Ajouter une tache dans la pile de tache
    public static void addTask(@NonNull final AsyncTask<Void, Integer, Boolean> task) {
        tasksList.add(task);
    }

    public static void clearDestroyList() {
        if (activitiesList != null && activitiesList.size() > 0) {
            for (Activity act : activitiesList) {
                if (!act.isFinishing())
                    act.finish();
            }
            activitiesList.clear();
        }
    }


    //Permet d'interrompre une tache
    public static void clearAsynTask() {
        if (tasksList != null && tasksList.size() > 0) {
            for (AsyncTask<Void, Integer, Boolean> mTask : tasksList) {
                if (!(null == mTask)) {
                    mTask.cancel(true);
                }
            }
            tasksList.clear();
        }
    }


    //Detruit le token
    public void clearToken(@NonNull final Context ctx) {
        try {
            Save.defaultSaveString(Constants.PREF_TOKEN, "", ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Verifie si le token jwt est valide
    public boolean isTokenValide() {
        try {
            token = Save.defaultLoadString(Constants.PREF_TOKEN, getApplicationContext());
            if (token != null && !token.equals("")) {
                if (token.equals(""))
                    return false;
                JWT jwt = new JWT(token);
                boolean isExpired = jwt.isExpired(0);
                return !isExpired;
            } else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    //Ajoute un token
    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        try {
            token = Save.defaultLoadString(Constants.PREF_TOKEN, getApplicationContext());
            Log.v("JWT TOKEN",token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }


    //Recupere le nom utilisateur des claims du jwt
    public String getUserId() {
        if (this.isTokenValide()) {
            JWT jwt = new JWT(getToken());
            Claim claim = jwt.getClaim("nameid");
            return claim.asString();

        } else {
            return "";
        }
    }


    //Verifie si le reseau est disponible
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void setup(SmartTabLayout layout) {

        final LayoutInflater inflater = LayoutInflater.from(layout.getContext());
        final Resources res = layout.getContext().getResources();

        layout.setCustomTabView(new SmartTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
                ImageView icon = (ImageView) inflater.inflate(R.layout.tab_icon, container,
                        false);
                switch (position) {
                    case 0:
                        // EMAIL
                        icon.setImageDrawable(new IconicsDrawable(container.getContext(), FontAwesome.Icon.faw_envelope)
                                .color(ContextCompat.getColor(container.getContext(), R.color.white))
                                .sizeDp(Constants.DRAWER_ICON_SIZE));
                        break;
                    case 1:
                        // Sms
                        icon.setImageDrawable(new IconicsDrawable(container.getContext(), FontAwesome.Icon.faw_mobile_alt)
                                .color(ContextCompat.getColor(container.getContext(), R.color.white))
                                .sizeDp(Constants.DRAWER_ICON_SIZE));
                        break;
                    case 2:
                        //ALERT
                        icon.setImageDrawable(new IconicsDrawable(container.getContext(), FontAwesome.Icon.faw_exclamation_triangle)
                                .color(ContextCompat.getColor(container.getContext(), R.color.white))
                                .sizeDp(Constants.DRAWER_ICON_SIZE));
                        break;
                    default:
                        break;
                }
                return icon;
            }
        });
    }


    public void setup_form(SmartTabLayout layout) {

        final LayoutInflater inflater = LayoutInflater.from(layout.getContext());
        final Resources res = layout.getContext().getResources();

        layout.setCustomTabView(new SmartTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
                ImageView icon = (ImageView) inflater.inflate(R.layout.tab_icon, container,
                        false);
                switch (position) {
                    case 0:
                        // FORM
                        icon.setImageDrawable(new IconicsDrawable(container.getContext(), FontAwesome.Icon.faw_wpforms)
                                .color(ContextCompat.getColor(container.getContext(), R.color.white))
                                .sizeDp(Constants.DRAWER_ICON_SIZE));
                        break;
                    case 1:
                        // IMAGE
                        icon.setImageDrawable(new IconicsDrawable(container.getContext(), FontAwesome.Icon.faw_image)
                                .color(ContextCompat.getColor(container.getContext(), R.color.white))
                                .sizeDp(Constants.DRAWER_ICON_SIZE));
                        break;
                    case 2:
                        // AUDIO
                        icon.setImageDrawable(new IconicsDrawable(container.getContext(), FontAwesome.Icon.faw_file_audio)
                                .color(ContextCompat.getColor(container.getContext(), R.color.white))
                                .sizeDp(Constants.DRAWER_ICON_SIZE));
                    default:
                        break;
                }
                return icon;
            }
        });
    }


    public @NonNull
    CopyOnWriteArrayList<Recipient> recipiencesStringToList(@NonNull String str) {
        CopyOnWriteArrayList<Recipient> list = new CopyOnWriteArrayList<>();
        try {
            JSONArray array = new JSONArray(str);
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
                reci.setFirstName(obj.getJSONObject("user")
                        .getString("first_name"));
                reci.setLastName(obj.getJSONObject("user")
                        .getString("last_name"));
                reci.setEmail(obj.getJSONObject("user")
                        .getString("email"));
                reci.setUserName(obj.getJSONObject("user")
                        .getString("username"));
                list.add(reci);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(Constants.APP_NAME, TAG + " CAN'T getRegions");
        }
        return list;
    }

    public @NonNull
    CopyOnWriteArrayList<Recipient> recipiencesJSONArrToList(@NonNull JSONArray array) {
        CopyOnWriteArrayList<Recipient> list = new CopyOnWriteArrayList<>();
        Log.v("appController:", array.toString());
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Recipient reci = new Recipient();
                reci.setIdOnServer(obj.getInt("id"));
                reci.setTel(obj.getString("telephone"));
                reci.setRef(obj.getString("reference"));
                reci.setDateCreate(obj.getString("create_at"));
                reci.setDeleted(obj.getBoolean("delete"));
                //reci.setFonction(obj.getInt("fonction"));
                reci.setAdresse(obj.getInt("adresse"));
                reci.setRole(obj.getInt("role"));
                reci.setEntreprise(obj.getInt("entreprise"));
                reci.setSuperieur(obj.getInt("superieur"));
                reci.setFirstName(obj.getJSONObject("user")
                        .getString("first_name"));
                reci.setLastName(obj.getJSONObject("user")
                        .getString("last_name"));
                reci.setEmail(obj.getJSONObject("user")
                        .getString("email"));
                reci.setUserName(obj.getJSONObject("user")
                        .getString("username"));
                list.add(reci);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(Constants.APP_NAME, TAG + " CAN'T getRegions");
        }


        return list;

    }

    public @Nullable
    JSONArray recipientListToJSONArr(@NonNull CopyOnWriteArrayList<Recipient> rList) throws Exception {
        Log.v(Constants.APP_NAME, TAG + " recipientToArray RUN");
        JSONArray jArr = new JSONArray();

        for (Recipient dm : rList) {
            JSONObject object = new JSONObject();

            object.put("id", dm.getIdOnServer());

            object.put("telephone", dm.getTel());
            object.put("reference", dm.getRef());
            object.put("create_at", dm.getDateCreate());
            object.put("delete", dm.isDeleted());
            object.put("fonction", dm.getFonction());
            object.put("adresse", dm.getAdresse());
            object.put("role", dm.getRole());
            object.put("entreprise", dm.getEntreprise());
            object.put("superieur", dm.getSuperieur());
            object.put("first_name", dm.getFirstName());
            object.put("last_name", dm.getLastName());
            object.put("email", dm.getEmail());
            object.put("username", dm.getUserName());

            jArr.put(object);
        }
        return jArr;
    }

    public void setAlert_categories(Map<String, Integer> alert_categories) {
        this.alert_categories = alert_categories;
    }

}
