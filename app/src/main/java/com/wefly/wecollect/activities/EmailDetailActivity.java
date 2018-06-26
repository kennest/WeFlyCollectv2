package com.wefly.wecollect.activities;

import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wefly.wecollect.presenters.FormActivity;
import com.wefly.wecollect.utils.NetworkWatcher;
import com.weflyagri.wecollect.R;

public class EmailDetailActivity extends FormActivity implements View.OnClickListener {

    private TextView tvObject, tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_detail);
        super.iniViewColors();
        iniViews();

        watcher = new NetworkWatcher(this, liMain);
        iniListeners();

        tvObject.setText(sEmail.getObject());
        tvContent.setText(sEmail.getContent());
    }

    private void iniViews() {
        tvObject = (EditText) findViewById(R.id.objectTView);
        tvContent = (EditText) findViewById(R.id.contentTView);
    }


    private void iniListeners() {
        for (AppCompatImageButton btn : butList) {
            btn.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        animMe(view, sEmail, null, null, this, watcher);
    }
}
