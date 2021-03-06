package com.wefly.wealert.activities;

import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.view.View;
import android.widget.TextView;

import com.wefly.wealert.presenters.FormActivity;
import com.wefly.wealert.utils.NetworkWatcher;
import com.weflyagri.wealert.R;

public class SmsDetailActivity extends FormActivity implements View.OnClickListener {

    private TextView tvSendBy, tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_detail);
        super.iniViewColors();
        iniViews();


        watcher = new NetworkWatcher(this, liMain);
        iniListeners();

        tvContent.setText(sSms.getContent());
        tvSendBy.setText(sSms.getSender());
    }

    private void iniViews() {
        tvContent = findViewById(R.id.contentTView);
        tvSendBy = findViewById(R.id.sendByTView);
    }

    private void iniListeners() {
        for (AppCompatImageButton btn : butList) {
            btn.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        animMe(view, null, sSms, null, this, watcher);
    }
}
