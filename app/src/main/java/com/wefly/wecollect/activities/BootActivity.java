package com.wefly.wecollect.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.fxn.pix.Pix;
import com.wefly.wecollect.fragments.SendDialogFrament;
import com.wefly.wecollect.models.Piece;
import com.wefly.wecollect.utils.AppController;
import com.wefly.wecollect.utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BootActivity extends AppCompatActivity {
    AppController appController = AppController.getInstance();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.boot_activity);
            Pix.start(BootActivity.this, Constants.REQUEST_CODE_SELECT_IMAGES, Constants.MAX_SELECT_COUNT);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        SendDialogFrament dialog=new SendDialogFrament();
        switch (requestCode) {
            case (100): {
                if (resultCode == Activity.RESULT_OK) {

                    ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                    List<Piece> pieces = new ArrayList<>();
//                    myAdapter.AddImage(returnValue);
//                    if (recyclerView.getVisibility() != View.VISIBLE)
//                        recyclerView.setVisibility(View.VISIBLE);

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
        Pix.start(BootActivity.this, Constants.REQUEST_CODE_SELECT_IMAGES, Constants.MAX_SELECT_COUNT);
    }
}
