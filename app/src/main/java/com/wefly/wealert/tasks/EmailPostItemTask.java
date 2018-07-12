package com.wefly.wealert.tasks;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.shashank.sony.fancytoastlib.FancyToast;
import com.wefly.wealert.models.Email;
import com.wefly.wealert.models.Piece;
import com.wefly.wealert.presenters.TaskPresenter;
import com.wefly.wealert.utils.AppController;
import com.wefly.wealert.utils.Constants;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by admin on 02/04/2018.
 */

public class EmailPostItemTask extends TaskPresenter {
    final Email email;
    private String response;
    private OnEmailSendListener listener;
    private String TAG = getClass().getSimpleName();
    private AppController appController;

    public EmailPostItemTask(@NonNull Email email) {
        this.email = email;
        appController = AppController.getInstance();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        FancyToast.makeText(appController.getApplicationContext(), "Envoi de l'Email...", FancyToast.LENGTH_LONG, FancyToast.INFO, false).show();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        EmailPostItemNetworkUtilities util = new EmailPostItemNetworkUtilities();
        try {
//          response = util.getResponseFromHttpUrl(alert.parcelleToJSONObjAsPostItem(), Constants.PARCELLE_SENT_URL );
//            Log.v(Constants.APP_NAME, TAG + " response " + response);
//
//            if (!response.trim().equals("") && !response.trim().equals(Constants.SERVER_ERROR) && !response.trim().equals(Constants.RESPONSE_EMPTY)){
//                JSONObject jOb = new JSONObject(response)
//                        .getJSONArray("reponse")
//                        .getJSONArray(0)
//                        .getJSONObject(0);
//                parcelle.setParcelleId(jOb.getInt("id_android"));
//                parcelle.setIdOnServer(jOb.getInt("id"));
//                parcelle.setDateSoumission(jOb.getString("date_soumission"));
//                parcelle.setDelete(Boolean.valueOf(jOb.getString("isDelete")));
//                parcelle.setNew(false);
//
//                return true;
//            }
//
            response = util.getResponseFromHttpUrl(email.getJSON(), Constants.SEND_EMAIL_URL);

            //Store the response in the sharedPref
            SharedPreferences sp = appController.getSharedPreferences("sent_data", 0);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("sent_response", response);
            editor.apply();

            Log.v(Constants.APP_NAME, TAG + " response " + response);
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(Constants.APP_NAME, TAG + "doInBackground Error ");
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean isOk) {
        super.onPostExecute(isOk);
        FancyToast.makeText(appController.getApplicationContext(), "Envoi terminé", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
        //Envoi des Pieces jointes
        if (appController.getPieceList().size() > 0) {
            Log.v("Email Post Execute", "RUN");
            new PieceUploadTask(appController.getPieceList(), email, null).execute();
        }
        notifyOnEmailSendListener(isOk, email);
    }

    public void setOnEmailSendListener(@NonNull OnEmailSendListener listener) {
        this.listener = listener;
    }

    private void notifyOnEmailSendListener(boolean isDone, @NonNull Email email) {
        if (listener != null) {
            try {
                if (isDone)
                    listener.onSendSucces(email);
                else
                    listener.onSendError(email);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface OnEmailSendListener {
        void onSendError(@NonNull Email e);

        void onSendSucces(@NonNull Email e);
    }

    public final class EmailPostItemNetworkUtilities {
        public String getResponseFromHttpUrl(@Nullable JSONObject jsonParam, @Nullable String url) {

            HttpClient httpclient;
            HttpPost httppost = new HttpPost(url);
            HttpResponse response;
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, Constants.VOLLEY_TIME_OUT);
            HttpConnectionParams.setSoTimeout(httpParameters, Constants.VOLLEY_TIME_OUT);
            httpclient = new DefaultHttpClient(httpParameters);
            Log.v(Constants.APP_NAME, TAG + "  doInBackGround url " + url + " Obj " + jsonParam.toString());

            try {
                httppost.setEntity(new StringEntity(jsonParam.toString(), "UTF-8"));
                httppost.setHeader("Content-type", "application/json;charset=UTF-8");
                httppost.setHeader("Accept-Type", "application/json");
                if (appController != null)
                    httppost.setHeader("Authorization", Constants.TOKEN_HEADER_NAME + appController.getToken());
                response = httpclient.execute(httppost);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                StringBuilder builder = new StringBuilder();
                for (String line; (line = reader.readLine()) != null; ) {
                    builder.append(line).append("\n");
                }
                Log.v(Constants.APP_NAME, TAG + "  doInBackGround parcelJson.toString response ");
                return builder.toString();
            } catch (UnsupportedEncodingException e) {
                Log.v(Constants.APP_NAME, TAG + "   doInBackGround UnsupportedEncodingException");
                e.printStackTrace();
            } catch (IOException e) {
                Log.v(Constants.APP_NAME, TAG + "   doInBackGround IOException");
                e.printStackTrace();
            }
            return Constants.SERVER_ERROR;
        }
    }

}
