package com.wefly.wecollect.tasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.wefly.wecollect.models.Email;
import com.wefly.wecollect.utils.AppController;
import com.wefly.wecollect.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EmailReceiveGetTask extends AsyncTask<String, Integer, List<Email>> {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private AppController appController;
    private String result;
    private List<Email> receiveEmails = new ArrayList<>();
    Integer count;
    String next;

    public EmailReceiveGetTask(AppController appController) {
        this.appController = appController;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected List<Email> doInBackground(String... strings) {
        try {
            result = getResponseFromUrl(Constants.EMAIL_RECEIVE_URL);
            receiveEmails.addAll(Objects.requireNonNull(extractEmail(result)));



            //Si on a encore d'autres page suivante on extrait les emails et on les ajoutes au emails recus
            JSONObject resultJSON;

            try {
                resultJSON = new JSONObject(result);
                int i = 1;
                count = Integer.parseInt(resultJSON.getString("count"));
                next = resultJSON.getString("next");
                do {
                    receiveEmails.addAll(Objects.requireNonNull(extractEmail(getResponseFromUrl(next))));
                    resultJSON = new JSONObject(getResponseFromUrl(next));
                    next = resultJSON.getString("next");
                    Log.v("Next value" + i, String.valueOf(i));
                } while (++i == (count-1));

                Log.v("Emails receive count " + i, String.valueOf(receiveEmails.size()));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.v("Email Receive Response", result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return receiveEmails;
    }

    @Override
    protected void onPostExecute(List<Email> emails) {
        super.onPostExecute(emails);
    }

    //Fonction de recuperation des infos sur le serveur
    private String getResponseFromUrl(@NonNull String url) throws IOException {
        Response response;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", Constants.TOKEN_HEADER_NAME + appController.getToken())
                .build();

        response = client.newCall(request).execute();

        return response.body().string();
    }

    //Fonction d'extraction des emails du flux JSON
    private List<Email> extractEmail(@NonNull String json) {
        List<Email> extractedList = new ArrayList<>();
        //On recupere les emails du flux JSON
        try {
            JSONObject resultJSON = new JSONObject(json);
            JSONArray emailsArray = resultJSON.getJSONArray("results");

            JSONObject emailReceive = emailsArray.getJSONObject(0);

            String expediteur = emailReceive.getJSONObject("email").getJSONObject("creer_par").getJSONObject("user").getString("username");

            Email email = new Email();


            email.setContent(emailReceive.getJSONObject("email").getString("contenu"));
            email.setDateCreated(emailReceive.getJSONObject("email").getString("date_de_creation"));
            email.setObject(emailReceive.getJSONObject("email").getString("objet"));
            email.setExpediteur(expediteur);

            extractedList.add(email);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return extractedList;
    }
}
