package com.wefly.wecollect.tasks;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.shashank.sony.fancytoastlib.FancyToast;
import com.wefly.wecollect.models.Alert;
import com.wefly.wecollect.models.Piece;
import com.wefly.wecollect.presenters.TaskPresenter;
import com.wefly.wecollect.services.GPSTracker;
import com.wefly.wecollect.utils.AppController;
import com.wefly.wecollect.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by admin on 02/04/2018.
 */

public class AlertPostItemTask extends TaskPresenter {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    final Alert alert;
    private String response;
    private List<Piece> pieces;
    private OnAlertSendListener listener;
    private String TAG = getClass().getSimpleName();
    private AppController appController;

    public AlertPostItemTask(@NonNull Alert alert) {
        this.alert = alert;
        appController = AppController.getInstance();
        this.pieces = appController.getPieceList();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        FancyToast.makeText(AppController.getInstance().getApplicationContext(), "Envoi de l'alert...", FancyToast.LENGTH_LONG, FancyToast.INFO, false).show();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        AlertPostItemNetworkUtilities util = new AlertPostItemNetworkUtilities();
        try {

            response = util.getResponseFromHttpUrl(Constants.SEND_ALERT_URL);

            Log.v("Alert Sent Response ", response.trim());

            //Store the response in the sharedPref
            SharedPreferences sp = appController.getSharedPreferences("alert_sent_data", 0);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("alert_sent_response", response);
            editor.apply();
            editor.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
//        try {
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
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.v(Constants.APP_NAME, TAG +"doInBackground Error ");
//        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean isOk) {
        super.onPostExecute(isOk);
        FancyToast.makeText(AppController.getInstance().getApplicationContext(), "Processus terminé", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();

        //Envoi des pieces jointes
        if (appController.getPieceList().size() > 0) {
            Log.v("Alert Post Execute", "RUN");
            new PieceUploadTask(pieces, null, alert).execute();
        }
    }

    public void setOnAlertSendListener(@NonNull OnAlertSendListener listener) {
        this.listener = listener;
    }

    public static interface OnAlertSendListener {
        void onSendError(@NonNull Alert e);

        void onSendSucces(@NonNull Alert e);
    }

    public final class AlertPostItemNetworkUtilities {
        public String getResponseFromHttpUrl(@NonNull String url) throws IOException {

//            HttpClient httpclient ;
//            HttpPost httppost = new HttpPost(url);
//            HttpResponse response;
//            HttpParams httpParameters = new BasicHttpParams();
//            HttpConnectionParams.setConnectionTimeout(httpParameters, Constants.VOLLEY_TIME_OUT);
//            HttpConnectionParams.setSoTimeout(httpParameters, Constants.VOLLEY_TIME_OUT);
//            httpclient = new DefaultHttpClient(httpParameters);
//            Log.v(Constants.APP_NAME, TAG + "  doInBackGround url " +url  + " Obj " + jsonParam.toString());
//
//            try {
//                httppost.setEntity(new StringEntity(jsonParam.toString(), "UTF-8"));
//                httppost.setHeader("Content-type", "application/json;charset=UTF-8");
//                httppost.setHeader("Accept-Type","application/json");
//                if (appController != null)
//                    httppost.setHeader("Authorization",Constants.TOKEN_HEADER_NAME + appController.getToken());
//                response = httpclient.execute(httppost);
//                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
//                StringBuilder builder = new StringBuilder();
//                for (String line = null; (line = reader.readLine()) != null;) {
//                    builder.append(line).append("\n");
//                }
//                Log.v(Constants.APP_NAME, TAG + "  doInBackGround parcelJson.toString response ");
//                return builder.toString();
//            }catch (UnsupportedEncodingException e) {
//                Log.v(Constants.APP_NAME, TAG + "   doInBackGround UnsupportedEncodingException" );
//                e.printStackTrace();
//            }catch (IOException e) {
//                Log.v(Constants.APP_NAME, TAG + "   doInBackGround IOException" );
//                e.printStackTrace();
//            }
//            return Constants.SERVER_ERROR;
//        }
            OkHttpClient client = new OkHttpClient();
            JSONObject json = new JSONObject();
            GPSTracker gps = new GPSTracker(appController.getApplicationContext());

            //Populate the json parameters
            try {
                json.put("titre", alert.getObject());
                json.put("contenu", alert.getContent());
                json.put("destinataires", alert.getRecipientsIds());
                json.put("longitude", gps.getLocation().getLongitude());
                json.put("latitude", gps.getLocation().getLatitude());
                json.put("date_alerte", "2018-06-15T16:59:49.352849Z");

                for (Map.Entry entry : appController.alert_categories.entrySet()) {
                    if (entry.equals(alert.getCategory()))
                        json.put("categorie", entry.getValue());
                }
                Log.v("ALERT JSON PARAMS", json.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }


            //Create the request
            RequestBody body = RequestBody.create(JSON, json.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", Constants.TOKEN_HEADER_NAME + appController.getToken())
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }
    }

}
