package com.wefly.wecollect.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.wefly.wecollect.adapters.emailAdapter;
import com.wefly.wecollect.models.Email;
import com.weflyagri.wecollect.R;

import java.util.List;

public class EmailListActivity extends Activity {

    private RecyclerView mRecyclerView;
    private List<Email> list;
    private emailAdapter emailAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_email_list);

        mRecyclerView=findViewById(R.id.emailRecyclerView);

//        list=new ArrayList<>();
//        list.add(new Email("Test 1","2018-06-15T16:59:49.352849Z"));
//        list.add(new Email("Test 2","2018-06-15T16:59:49.352849Z"));

    }
}
