package com.wefly.wecollect.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wefly.wecollect.model.Alert;
import com.wefly.wecollect.presenter.DBActivity;
import com.wefly.wecollect.presenter.FormActivity;
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
        tvSender.setText(sAlert.getSender());
    }

    private void iniViews() {
        tvSender            =   (TextView) findViewById(R.id.senderTView);
        tvObject            =   (TextView) findViewById(R.id.objectTView);
        tvContent           =   (TextView) findViewById(R.id.contentTView);
    }


    private void iniListeners() {
        for (AppCompatImageButton btn : butList){
            btn.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        animMe(view,null, null,sAlert, this, watcher);
    }
}
