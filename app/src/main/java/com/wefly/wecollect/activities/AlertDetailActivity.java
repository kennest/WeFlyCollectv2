package com.wefly.wecollect.activities;

import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.view.View;
import android.widget.TextView;

import com.wefly.wecollect.presenters.FormActivity;
import com.wefly.wecollect.utils.NetworkWatcher;
import com.weflyagri.wecollect.R;

public class AlertDetailActivity extends FormActivity implements View.OnClickListener {
    private TextView tvSender, tvObject, tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_detail);
        super.iniViewColors();
        iniViews();
        watcher = new NetworkWatcher(this, liMain);

        iniListeners();

        tvContent.setText(sAlert.getContent());
        tvObject.setText(sAlert.getObject());
    }

    private void iniViews() {
        tvSender = findViewById(R.id.senderTView);
        tvObject = findViewById(R.id.objectTView);
        tvContent = findViewById(R.id.contentTView);
    }


    private void iniListeners() {
        for (AppCompatImageButton btn : butList) {
            btn.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        animMe(view, null, null, sAlert, this, watcher);
    }
}
