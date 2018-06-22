package com.wefly.wecollect.presenter;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

/**
 * Created by admin on 23/05/2018.
 */

public class LocationActivity extends DBActivity {
    protected LocationPresenter locaPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locaPresenter = new LocationPresenter(this);
        enableLocUpdate();
    }

    private void enableLocUpdate() {
        if (isReqDone){
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (locaPresenter == null)
                                        locaPresenter = new LocationPresenter(LocationActivity.this);
                                    if (!locaPresenter.isLocationEnabled())
                                        locaPresenter.showLocationServicesRequireDialog();
                                    else{
                                        startLocationService();
                                    }

                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }, 500);
        }

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        enableLocUpdate();
    }
}
