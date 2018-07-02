package com.wefly.wecollect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.wefly.wecollect.MainActivity;
import com.wefly.wecollect.presenters.BaseActivity;
import com.wefly.wecollect.utils.AppController;
import com.weflyagri.wecollect.R;

public class LoadingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        AppController.addToDestroyList(this);
        Handler h = new Handler();
        h.postDelayed(() -> runOnUiThread(() -> startActivity(new Intent(getApplicationContext(), BootActivity.class))), 2000);
    }

}
