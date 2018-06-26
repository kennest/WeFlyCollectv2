package com.wefly.wecollect.tasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.wefly.wecollect.utils.AppController;
import com.wefly.wecollect.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CategoryGetTask extends AsyncTask<Map<String, Integer>, Integer, Map<String, Integer>> {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private Map<String, Integer> categories;
    private AppController appController;


    public CategoryGetTask(AppController appController) {
        this.appController = appController;
    }

    @Override
    protected Map<String, Integer> doInBackground(Map<String, Integer>... maps) {
        try {
            categories = getCategory(Constants.ALERT_CATEGORY_URL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return categories;
    }

    private Map<String, Integer> getCategory(@NonNull String url) throws IOException {

        OkHttpClient client = new OkHttpClient();
        Map<String, Integer> list_categories = new HashMap<>();
        String result = "";

        Request request = new Request.Builder().url(url)
                .addHeader("Authorization", Constants.TOKEN_HEADER_NAME + appController.getToken())
                .build();
        Response response = client.newCall(request).execute();

        result = response.body().string();
        Log.v("CATEGORY JSON :", result.trim());

        try {
            JSONObject response_data = new JSONObject(result);
            JSONArray response_category = new JSONArray(response_data.getString("results"));

            for (int i = 0; i < response_category.length(); i++) {
                JSONObject obj = new JSONObject(response_category.getString(i));
                list_categories.put(obj.getString("nom"), obj.getInt("id"));
            }
            appController.setAlert_categories(list_categories);

            Log.v("CATEGORY LIST:", response_category.toString());


            for (Map.Entry entry : list_categories.entrySet()) {
                Log.v("Entry " + entry.toString(), "key" + entry.getKey() + " Value" + entry.getValue());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list_categories;
    }
}
