package com.wefly.wealert.tasks;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.wefly.wealert.models.Sms;
import com.wefly.wealert.presenters.DBActivity;
import com.wefly.wealert.presenters.TaskPresenter;
import com.wefly.wealert.utils.AppController;
import com.wefly.wealert.utils.Constants;

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

public class SmsReceiveGetTask extends TaskPresenter {
    String response = "";
    private DBActivity act;
    private OnSmsReceiveDownloadCompleteListener listener;
    private String TAG = getClass().getSimpleName();
    private AppController appController;
    private CopyOnWriteArrayList<Sms> list = new CopyOnWriteArrayList<>();
    private boolean hasPrev, hasNext;
    private String prev = "", next = "";
    private String url = "";

    public SmsReceiveGetTask(@NonNull DBActivity activity, @NonNull String url) {
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
            SmsReceiveNetworkUtilities util = new SmsReceiveNetworkUtilities();
            response = util.getResponseFromHttpUrl(url);
            Log.v(Constants.APP_NAME, TAG + "response" + response);

            if (!response.trim().equals("") && !response.trim().equals(Constants.SERVER_ERROR) && !response.trim().contains(Constants.RESPONSE_ERROR_HTML)) {
                if (response.trim().equals(Constants.RESPONSE_EMPTY)) {
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

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    JSONObject smsOb = obj.getJSONObject("sms");
                    Sms sms = new Sms();
                    sms.setRead(obj.getBoolean("lu"));
                    sms.setIdOnServer(smsOb.getInt("id"));
                    sms.setDateCreated(smsOb.getString("date_de_creation"));
                    sms.setContent(smsOb.getString("contenu"));
                    sms.setAuthorId(smsOb.getInt("creer_par"));


                    list.add(sms);
                }

                //list full
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // Network Error
    }

    @Override
    protected void onPostExecute(Boolean isOk) {
        super.onPostExecute(isOk);
        notifyOnSmsReceiveDownloadCompleteListener(isOk, list, hasPrev, hasNext, prev, next);

    }

    public void setOnSmsReceiveDownloadCompleteListener(@NonNull OnSmsReceiveDownloadCompleteListener listener) {
        this.listener = listener;

    }

    private void notifyOnSmsReceiveDownloadCompleteListener(boolean isOK, @NonNull CopyOnWriteArrayList<Sms> list, boolean hPrev, boolean hNext, @NonNull String prev, @NonNull String next) {
        if (listener != null) {
            try {
                if (isOK) {
                    listener.onSmsRecDownloadSucces(list, hPrev, hNext, prev, next);
                } else {
                    listener.onSmsRecDownloadError(hPrev, hNext, prev, next);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface OnSmsReceiveDownloadCompleteListener {
        void onSmsRecDownloadError(boolean hPrev, boolean hNext, @NonNull String prev, @NonNull String next);

        void onSmsRecDownloadSucces(@NonNull CopyOnWriteArrayList<Sms> list, boolean hPrev, boolean hNext, @NonNull String prev, @NonNull String next);
    }

    public final class SmsReceiveNetworkUtilities {
        public String getResponseFromHttpUrl(@Nullable String base_url) {

            HttpClient httpclient;
            HttpGet httpget = new HttpGet(base_url);
            HttpResponse response;
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, Constants.VOLLEY_TIME_OUT);
            HttpConnectionParams.setSoTimeout(httpParameters, Constants.VOLLEY_TIME_OUT);
            httpclient = new DefaultHttpClient(httpParameters);
            Log.v(Constants.APP_NAME, TAG + "doInBackGround url " + base_url);

            try {
                httpget.setHeader("Content-type", "application/json;charset=UTF-8");
                httpget.setHeader("Accept-Type", "application/json");
                if (appController != null)
                    httpget.setHeader("Authorization", Constants.TOKEN_HEADER_NAME + appController.getToken());
                response = httpclient.execute(httpget);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                StringBuilder builder = new StringBuilder();
                for (String line = null; (line = reader.readLine()) != null; ) {
                    builder.append(line).append("\n");
                }
                Log.v(Constants.APP_NAME, TAG + " doInBackGround toString response ");
                return builder.toString();
            } catch (UnsupportedEncodingException e) {
                Log.v(Constants.APP_NAME, TAG + " doInBackGround UnsupportedEncodingException");
                e.printStackTrace();
            } catch (IOException e) {
                Log.v(Constants.APP_NAME, TAG + " IOException");
                e.printStackTrace();
            }
            return Constants.SERVER_ERROR;
        }

    }
}
