package com.wefly.wecollect.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.wefly.wecollect.adapters.AlertDetailAdapter;
import com.wefly.wecollect.models.Alert;
import com.wefly.wecollect.tasks.AlertReceiveGetTask;
import com.wefly.wecollect.utils.AppController;
import com.weflyagri.wecollect.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AlertDetailActivity extends Activity {
    List<Alert> alertList = new ArrayList<>();
    AppController appController;
    RecyclerView cardRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_detail);
        //On recupere le card recycler view et on lui associe son adapter
        cardRecyclerView = findViewById(R.id.cardRecyclerView);
        cardRecyclerView.setAdapter(new AlertDetailAdapter(appController.getApplicationContext(), alertList));
    }
}
