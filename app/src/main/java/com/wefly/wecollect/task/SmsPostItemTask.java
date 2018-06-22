package com.wefly.wecollect.task;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.wefly.wecollect.model.Sms;
import com.wefly.wecollect.presenter.DBActivity;
import com.wefly.wecollect.presenter.DataBasePresenter;
import com.wefly.wecollect.presenter.TaskPresenter;
import com.wefly.wecollect.utils.AppController;
import com.wefly.wecollect.utils.Constants;
import com.weflyagri.wecollect.R;

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

import pl.tajchert.waitingdots.DotsTextView;

/**
 * Created by admin on 02/04/2018.
 */

public  class SmsPostItemTask extends TaskPresenter {
    final Sms sms;
    private String response;
    private OnSmsSendListener listener;
    private String TAG = getClass().getSimpleName();
    private boolean isNetworkError;
    private AppController appController;
    private DotsTextView dotsTView;
    private Activity act;

    public SmsPostItemTask(@NonNull Sms sms, @NonNull final DBActivity act){
        this.sms = sms;
        this.act = act;
        appController = AppController.getInstance();
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try{
            dotsTView = (DotsTextView) act.findViewById(R.id.dots);
            TextView textView = (TextView) act.findViewById(R.id.loadingTitleTView);
            if (textView != null)
                textView.setText(R.string.sending_short);
            dotsTView.showAndPlay();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        SmsPostItemNetworkUtilities util = new SmsPostItemNetworkUtilities();
        try {
            response = util.getResponseFromHttpUrl(sms.toPostItem(), Constants.SEND_SMS_URL );
            Log.v(Constants.APP_NAME, TAG + " response " + response);

            if (!response.trim().equals("") && !response.trim().equals(Constants.SERVER_ERROR) && !response.trim().contains(Constants.RESPONSE_ERROR_HTML)){
                //No network error
                // post update
                if(response.trim().contains(Constants.RESPONSE_ERROR_SMS) || response.trim().contains(Constants.RESPONSE_ERROR_SMS_NOT_SENT)){
                    // sms not sent
                    return false;
                }
                if (DataBasePresenter.getInstance().addSms(sms)){
                    DataBasePresenter.getInstance().close();
                    return true;
                }
            }else
                isNetworkError = true;

        } catch (Exception e) {
            e.printStackTrace();
            Log.v(Constants.APP_NAME, TAG +"doInBackground Error ");
        }
        return false;

    }

    @Override
    protected void onPostExecute(Boolean isOk) {
        super.onPostExecute(isOk);

        try {
            dotsTView.hideAndStop();
            notifyOnSmsSendListener(isOk, sms);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void setOnSmsSendListener(@NonNull OnSmsSendListener listener) {
        this.listener = listener;

    }

    public static interface OnSmsSendListener {
        void onSendError(@NonNull Sms s);
        void onSendErrorNetwork(@NonNull Sms s);
        void onSendSucces(@NonNull Sms s);
    }

    private void notifyOnSmsSendListener(boolean isDone, @NonNull Sms sms) {
        if (listener != null){
            try {
                if (isNetworkError)
                    listener.onSendErrorNetwork(sms);
                else {
                    if (isDone)
                        listener.onSendSucces(sms);
                    else
                        listener.onSendError(sms);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }


    }

    public final class SmsPostItemNetworkUtilities {
        public String getResponseFromHttpUrl(@NonNull JSONObject jsonParam, @Nullable String url) throws IOException {

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
