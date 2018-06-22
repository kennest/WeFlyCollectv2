package com.wefly.wecollect.task;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.wefly.wecollect.model.Recipient;
import com.wefly.wecollect.model.Sms;
import com.wefly.wecollect.presenter.DBActivity;
import com.wefly.wecollect.presenter.TaskPresenter;
import com.wefly.wecollect.utils.AppController;
import com.wefly.wecollect.utils.Constants;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by admin on 02/04/2018.
 */

public class SmsSentGetTask extends TaskPresenter{
    private DBActivity act;
    String response = "";
    private OnSmsSentDownloadCompleteListener listener;
    private AppController appController;
    private boolean hasPrev, hasNext;
    private String prev = "", next = "";
    private String url = "";
    private CopyOnWriteArrayList<Sms> list = new CopyOnWriteArrayList<>();
    private String TAG = getClass().getSimpleName();

    public SmsSentGetTask(@NonNull DBActivity activity, @NonNull String url){
        this.act = activity;
        this.url = url;
        appController = AppController.getInstance();
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            SmsSentTaskNetworkUtilities util = new SmsSentTaskNetworkUtilities();
            response = util.getResponseFromHttpUrl(url);
            Log.v(Constants.APP_NAME, TAG +  "response" + response);

            if (!response.trim().equals("") && !response.trim().equals(Constants.SERVER_ERROR) && !response.trim().contains(Constants.RESPONSE_ERROR_HTML)){
                if(response.trim().equals(Constants.RESPONSE_EMPTY)){
                    // List Empty
                    return true;
                }

                // Response Ok
                JSONObject object = new JSONObject(response);
                hasNext = !(object
                        .getString("next").equals("null"));
                if (hasNext)
                    next = object.getString("next");
                hasPrev = !(object
                        .getString("previous").equals("null"));
                if (hasPrev)
                    prev = object.getString("previous");

                JSONArray arr = object
                        .getJSONArray("results");

                for (int  i = 0; i < arr.length(); i++){
                    JSONObject obj = arr.getJSONObject(i);

                    JSONArray recipArray = null;
                    CopyOnWriteArrayList<Recipient> rList = new CopyOnWriteArrayList<>();
                    Sms sms = new Sms();

                    // Sms content
                    sms.setDateCreated(obj
                            .getJSONObject("creer_par")
                            .getString("create_at"));
                    sms.setContent(obj.getString("contenu"));


                    // Recipients
                    recipArray = obj
                            .getJSONArray("destinataires");
                    for (int j = 0; j < recipArray.length(); j++){
                        JSONObject objRec = recipArray.getJSONObject(j);
                        Recipient reci = new Recipient();
                        reci.setIdOnServer(objRec.getInt("id"));
                        reci.setTel(objRec.getString("telephone"));
                        reci.setRef(objRec.getString("reference"));
                        reci.setDateCreate(objRec.getString("create_at"));
                        reci.setDeleted(objRec.getBoolean("delete"));
                        reci.setFonction(objRec.getInt("fonction"));
                        reci.setAdresse(objRec.getInt("adresse"));
                        reci.setRole(objRec.getInt("role"));
                        reci.setEntreprise(objRec.getInt("entreprise"));
                        reci.setSuperieur(objRec.getInt("superieur"));

                        reci.setFirstName(objRec.getJSONObject("user")
                                .getString("first_name"));
                        reci.setLastName(objRec.getJSONObject("user")
                                .getString("last_name"));
                        reci.setEmail(objRec.getJSONObject("user")
                                .getString("email"));
                        reci.setUserName(objRec.getJSONObject("user")
                                .getString("username"));

                        rList.add(reci);
                    }

                    sms.setRecipients(rList);


                    list.add(sms);
                }

                //list full
                return true;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return  false;
    }

    @Override
    protected void onPostExecute(Boolean isOk) {
        super.onPostExecute(isOk);
        notifyOnSmsSentDownloadCompleteListener(isOk, list, hasPrev, hasNext, prev, next);

    }

    public void setOnSmsSentDownloadCompleteListener(@NonNull OnSmsSentDownloadCompleteListener listener) {
        this.listener = listener;

    }

    public static interface OnSmsSentDownloadCompleteListener {
        void onSmsSentDownloadError(boolean hPrev, boolean hNext, @NonNull String prev, @NonNull String next);
        void onSmsSentDownloadSucces(@NonNull CopyOnWriteArrayList<Sms> list, boolean hPrev, boolean hNext , @NonNull String prev, @NonNull String next);
    }

    private void notifyOnSmsSentDownloadCompleteListener(boolean isOK, @NonNull CopyOnWriteArrayList<Sms> list, boolean hPrev, boolean hNext, @NonNull String prev, @NonNull String next) {
        if (listener != null){
            try {
                if (isOK){
                    listener.onSmsSentDownloadSucces(list, hPrev, hNext, prev, next);
                }else{
                    listener.onSmsSentDownloadError(hPrev, hNext, prev, next);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }



    public final class SmsSentTaskNetworkUtilities {
        public String getResponseFromHttpUrl( @Nullable String base_url) throws IOException {

            HttpClient httpclient ;
            HttpGet httpget = new HttpGet(base_url);
            HttpResponse response;
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, Constants.VOLLEY_TIME_OUT);
            HttpConnectionParams.setSoTimeout(httpParameters, Constants.VOLLEY_TIME_OUT);
            httpclient = new DefaultHttpClient(httpParameters);
            Log.v(Constants.APP_NAME, TAG + "doInBackGround url " +base_url );

            try {
                httpget.setHeader("Content-type", "application/json;charset=UTF-8");
                httpget.setHeader("Accept-Type","application/json");
                if (appController != null)
                    httpget.setHeader("Authorization",Constants.TOKEN_HEADER_NAME + appController.getToken());
                response = httpclient.execute(httpget);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                StringBuilder builder = new StringBuilder();
                for (String line = null; (line = reader.readLine()) != null;) {
                    builder.append(line).append("\n");
                }
                Log.v(Constants.APP_NAME, TAG + " doInBackGround toString response ");
                return builder.toString();
            }catch (UnsupportedEncodingException e) {
                Log.v(Constants.APP_NAME, TAG + " doInBackGround UnsupportedEncodingException" );
                e.printStackTrace();
            }catch (IOException e) {
                Log.v(Constants.APP_NAME, TAG +" IOException" );
                e.printStackTrace();
            }
            return Constants.SERVER_ERROR;
        }

    }
}
