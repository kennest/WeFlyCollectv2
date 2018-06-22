package com.wefly.wecollect.task;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.kosalgeek.android.imagebase64encoder.ImageBase64;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.wefly.wecollect.model.Piece;
import com.wefly.wecollect.utils.AppController;
import com.wefly.wecollect.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class PieceUploadTask extends AsyncTask<String, Integer, String> {
    private List<Piece> pieces;
    private AppController appController;
    private String response;
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpeg");
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public PieceUploadTask(@NonNull List<Piece> pieces) {
        this.pieces = pieces;
        appController = AppController.getInstance();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        FancyToast.makeText(AppController.getInstance().getApplicationContext(),"Envoi des pieces jointes...",FancyToast.LENGTH_LONG,FancyToast.INFO,false).show();
    }

    @Override
    protected String doInBackground(String... strings) {
        PieceUploadNetworkUtilities util = new PieceUploadNetworkUtilities();
        String result = "";
        try {
            result = util.uploadImage(pieces, "test", Constants.SEND_FILE_URL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        FancyToast.makeText(AppController.getInstance().getApplicationContext(),"Processus terminé!",FancyToast.LENGTH_LONG,FancyToast.INFO,false).show();
    }

    private final class PieceUploadNetworkUtilities {
        private String uploadImage(@Nullable List<Piece> images, @Nullable String imageName, @Nullable String url) throws IOException {
            OkHttpClient client = new OkHttpClient();

            JSONObject dataJson = new JSONObject();
            String encodedImage = "";
            String result = "";

            for (Piece p : images) {
                try {
                    try {
                        encodedImage = ImageBase64
                                .with(appController.getApplicationContext())
                                .encodeFile(p.getContentUrl().getPath());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    //Init Json data
                    dataJson.put("piece_name", "test.jpg");
                    dataJson.put("piece_b64", encodedImage.trim());
                    dataJson.put("email", 53);

                    Log.v("PieceUploadTask base64" + "\n", encodedImage.trim());

                    //Build request body
                    RequestBody body = RequestBody.create(JSON, dataJson.toString());

                    Request request = new Request.Builder().url(url)
                            .addHeader("Authorization", Constants.TOKEN_HEADER_NAME + appController.getToken())
                            .post(body)
                            .build();
                    //Execute
                    Response response = client.newCall(request).execute();
                    result = response.body().string();
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Log.v("Piece Upload Result", result);
            return result;
        }

    }
}
