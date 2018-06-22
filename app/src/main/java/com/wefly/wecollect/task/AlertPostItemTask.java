package com.wefly.wecollect.task;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import com.wefly.wecollect.model.Alert;
import com.wefly.wecollect.presenter.TaskPresenter;
import com.wefly.wecollect.utils.AppController;
import com.wefly.wecollect.utils.Constants;

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

/**
 * Created by admin on 02/04/2018.
 */

public  class  AlertPostItemTask extends TaskPresenter {
    final Alert alert;
    private String response;
    private OnAlertSendListener listener;
    private String TAG = getClass().getSimpleName();
    private AppController appController;

    public AlertPostItemTask(@NonNull Alert alert){
        this.alert = alert;
        appController = AppController.getInstance();
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        AlertPostItemNetworkUtilities util = new AlertPostItemNetworkUtilities();
        try {
//            response = util.getResponseFromHttpUrl(alert.parcelleToJSONObjAsPostItem(), Constants.PARCELLE_SENT_URL );
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

        } catch (Exception e) {
            e.printStackTrace();
            Log.v(Constants.APP_NAME, TAG +"doInBackground Error ");
        }


        return false;

    }

    @Override
    protected void onPostExecute(Boolean isOk) {
        super.onPostExecute(isOk);
        notifyOnAlertSendListener(isOk, alert);
    }

    public void setOnParcelleSendListener(@NonNull OnAlertSendListener listener) {
        this.listener = listener;

    }

    public static interface OnAlertSendListener {
        void onSendError(@NonNull Alert a);
        void onSendSucces(@NonNull Alert a);
    }

    private void notifyOnAlertSendListener(boolean isDone, @NonNull Alert alert) {
        if (listener != null ){
            try {
                if (isDone)
                    listener.onSendSucces(alert);
                else
                    listener.onSendError(alert);
            }catch (Exception e){
                e.printStackTrace();
            }

        }


    }

    public final class AlertPostItemNetworkUtilities {
        public String getResponseFromHttpUrl(@Nullable JSONObject jsonParam,@Nullable  String url) throws IOException {

            HttpClient httpclient ;
            HttpPost httppost = new HttpPost(url);
            HttpResponse response;
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, Constants.VOLLEY_TIME_OUT);
            HttpConnectionParams.setSoTimeout(httpParameters, Constants.VOLLEY_TIME_OUT);
            httpclient = new DefaultHttpClient(httpParameters);
            Log.v(Constants.APP_NAME, TAG + "  doInBackGround url " +url  + " Obj " + jsonParam.toString());

            try {
                httppost.setEntity(new StringEntity(jsonParam.toString(), "UTF-8"));
                httppost.setHeader("Content-type", "application/json;charset=UTF-8");
                httppost.setHeader("Accept-Type","application/json");
                if (appController != null)
                    httppost.setHeader("Authorization",Constants.TOKEN_HEADER_NAME + appController.getToken());
                response = httpclient.execute(httppost);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                StringBuilder builder = new StringBuilder();
                for (String line = null; (line = reader.readLine()) != null;) {
                    builder.append(line).append("\n");
                }
                Log.v(Constants.APP_NAME, TAG + "  doInBackGround parcelJson.toString response ");
                return builder.toString();
            }catch (UnsupportedEncodingException e) {
                Log.v(Constants.APP_NAME, TAG + "   doInBackGround UnsupportedEncodingException" );
                e.printStackTrace();
            }catch (IOException e) {
                Log.v(Constants.APP_NAME, TAG + "   doInBackGround IOException" );
                e.printStackTrace();
            }
            return Constants.SERVER_ERROR;
        }
    }

}
