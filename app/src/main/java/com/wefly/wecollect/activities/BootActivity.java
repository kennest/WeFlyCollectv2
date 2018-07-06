package com.wefly.wecollect.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.fxn.pix.Pix;
import com.wefly.wecollect.fragments.SendDialogFrament;
import com.wefly.wecollect.models.Piece;
import com.wefly.wecollect.models.Recipient;
import com.wefly.wecollect.services.LocationProviderService;
import com.wefly.wecollect.tasks.RecipientGetTask;
import com.wefly.wecollect.utils.AppController;
import com.wefly.wecollect.utils.Constants;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BootActivity extends AppCompatActivity {
    AppController appController = AppController.getInstance();
    List<Recipient> recipients=new ArrayList<>();
    SendDialogFrament dialog=new SendDialogFrament();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //We start Location service
        startLocationService();
        //setContentView(R.layout.boot_activity);
            Pix.start(BootActivity.this, Constants.REQUEST_CODE_SELECT_IMAGES, Constants.MAX_SELECT_COUNT);
        //We pass the recipients list to the dialog
        try {
            recipients=new RecipientGetTask().execute().get();
            Bundle bundle=new Bundle();
            bundle.putSerializable("recipients_list",(Serializable)recipients);
            dialog.setArguments(bundle);
        } catch (ExecutionException |InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (100): {
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                    List<Piece> pieces = new ArrayList<>();

                    int i=0;
                    //On recupere la liste des URL des images
                    for (String r : returnValue) {
                        Piece p=new Piece();
                        p.setIndex(i++);
                        p.setUrl(r.trim());
                        p.setContentUrl(Uri.fromFile(new File(r.trim())));
                        Log.v("Image URL", r);
                        pieces.add(p);
                    }
                    appController.setPieceList(pieces);
                    dialog.show(getSupportFragmentManager(),"SEND STEP");
                    Log.v("PIECE SIZE ", String.valueOf(appController.getPieceList().size()));
                }
            }
            break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dialog.dismiss();
        appController.setPieceList(new ArrayList<>());
        Pix.start(BootActivity.this, Constants.REQUEST_CODE_SELECT_IMAGES, Constants.MAX_SELECT_COUNT);
    }

    protected void startLocationService() {
        Intent intent = new Intent(this, LocationProviderService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startService(intent);
    }

}
