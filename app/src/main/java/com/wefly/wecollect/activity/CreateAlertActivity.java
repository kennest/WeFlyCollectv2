package com.wefly.wecollect.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fxn.pix.Pix;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.pchmn.materialchips.ChipsInput;
import com.wefly.wecollect.adapter.imageAdapter;
import com.wefly.wecollect.model.Alert;
import com.wefly.wecollect.model.Recipient;
import com.wefly.wecollect.model.Sms;
import com.wefly.wecollect.presenter.FormActivity;
import com.wefly.wecollect.utils.Constants;
import com.wefly.wecollect.utils.NetworkWatcher;
import com.wefly.wecollect.utils.design.AnimeView;
import com.weflyagri.wecollect.R;

import java.util.ArrayList;
import java.util.List;

public class CreateAlertActivity extends FormActivity implements View.OnClickListener {

    private EditText edObject, edContent;
    private RelativeLayout rlMain;
    protected Alert alert;
    View vForm, vImages;
    RecyclerView recyclerView;
    imageAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_alert);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        iniViews();
        iniViewAndColors();

        watcher = new NetworkWatcher(this, rlMain);

        iniListeners();
        iniChipInput();
    }

    private void iniViewAndColors(){
        bCancel         =  vForm.findViewById(R.id.btnCancel);
        bClose          =  vForm.findViewById(R.id.btnClose);
        bSend           =  vForm.findViewById(R.id.btnSend);

        rlMain          = findViewById(R.id.Rlayout);
        liMain          = null; // free memory in super

        butList.clear();
        butList.add(bCancel);
        butList.add(bClose);
        butList.add(bSend);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // below lollipop
            ColorStateList csl = ColorStateList.valueOf(Color.TRANSPARENT);
            for (AppCompatImageButton btn : butList){
                btn.setSupportBackgroundTintList(csl);
            }
        }
    }

    private void iniViews() {
        // Form fragment
        vForm = LayoutInflater.from(getBaseContext()).inflate(R.layout.fragment_create_alert_form, null, false);

        // images fragment
        vImages = LayoutInflater.from(getBaseContext()).inflate(R.layout.fragment_attachment, null, false);

        edObject          =          (EditText)   vForm.findViewById(R.id.objectEdText);
        edContent         =          (EditText)   vForm.findViewById(R.id.contentEdText);
        ciRecipients      =          (ChipsInput) vForm.findViewById(R.id.recipientsCi);

        // Setup custom tab
        ViewGroup tab = (ViewGroup) findViewById(R.id.tab);
        tab.addView(LayoutInflater.from(this).inflate(R.layout.fragment_main, tab, false));

        ViewPager viewPager = findViewById(R.id.viewpager);
        SmartTabLayout viewPagerTab = findViewById(R.id.viewpagertab);
        if (appController != null)
            appController.setup_form(viewPagerTab);


        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public boolean isViewFromObject(final View view, final Object object) {
                return view.equals(object);
            }

            @Override
            public void destroyItem(final View container, final int position, final Object object) {
                ((ViewPager) container).removeView((View) object);
            }

            @Override
            public Object instantiateItem(final ViewGroup container, final int position) {
                final View view;

                try {
                    if (position == 0){
                        // image fragment
                        view = vForm;
                    }else {
                        // image fragment
                        view = vImages;
                    }
                    container.addView(view);

                }catch (Exception e){
                    e.printStackTrace();
                    View mView = LayoutInflater.from(getBaseContext()).inflate(R.layout.fragment_create_alert_form, null, false);
                    container.addView(mView);
                    return mView;
                }


                return view;

            }
        });
        viewPagerTab.setViewPager(viewPager);
        viewPagerTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Setup multiimage picker
        recyclerView = vImages.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new imageAdapter(this);
        recyclerView.setAdapter(myAdapter);
    }

    private void iniListeners() {
        for (AppCompatImageButton btn : butList){
            btn.setOnClickListener(this);
        }

        vImages.findViewById(R.id.liSelectImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AnimeView(view, new AnimeView.OnAnimationEndCallBack() {
                    @Override
                    public void onEnd(@NonNull View view) {
                        Pix.start(CreateAlertActivity.this, Constants.REQUEST_CODE_SELECT_IMAGES, Constants.MAX_SELECT_COUNT);
                    }
                }).startAnimation();
            }
        });


    }

    @Override
    public void onClick(View view) {
        sAlert.setObject(edObject.getText().toString().trim());
        sAlert.setContent(edContent.getText().toString().trim());
        saveInput();
        animMe(view,null, null,sAlert, this, watcher);
    }

    private void saveInput() {
        if (sAlert != null){
            sAlert.setContent(edContent.getText().toString().trim());
            recipientsSelected.clear();
            List<Recipient> list = (List<Recipient>) ciRecipients.getSelectedChipList();
            for (Recipient mDm: list){
                recipientsSelected.add(mDm);
            }
            if (recipientsSelected.size() > 0)
                sAlert.setRecipients(recipientsSelected);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (100): {
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                    myAdapter.AddImage(returnValue);
                    if (recyclerView.getVisibility() != View.VISIBLE)
                        recyclerView.setVisibility(View.VISIBLE);
                }
                break;
            }
        }
    }

    @Override
    public void onConnected() {
        super.onConnected();
        if (selected == 1){
            if (alert != null){
                // SMS
                lockSendBtn();
                onDisplayUi(alert, false,false, true);
                super.sendAlert(alert);
            }
        }
        selected = 0;
    }

    private void onDisplayUi(@NonNull Alert sms, boolean isRestoreState, boolean isSaving, boolean isSending){

        if (isRestoreState){
            if (edContent != null && ciRecipients != null){
                edContent.setText(sms.getContent());

                //Existing Sms
                // old Selected recipients
                if (sms.getRecipients().size() > 0){
                    for (Recipient dm: sms.getRecipients()){
                        ciRecipients.addChip(dm);
                    }
                }

            }

        }

        if (isSaving){
            if (srvMain != null && liProgress != null){
                TextView textView =liProgress.findViewById(R.id.loadingTitleTView);
                if (textView != null)
                    textView.setText(R.string.saving);
                srvMain.setVisibility(View.INVISIBLE);
                liProgress.setVisibility(View.VISIBLE);
            }
        }else {
            showUI();
        }

        if (isSending){
            if (srvMain != null && liProgress != null){
                srvMain.setVisibility(View.INVISIBLE);
                liProgress.setVisibility(View.VISIBLE);
            }
        }else{
            showUI();
        }
    }

    private void showUI() {
        if (srvMain != null && liProgress != null){
            if (srvMain.getVisibility() != View.VISIBLE)
                srvMain.setVisibility(View.VISIBLE);
            if (liProgress.getVisibility() != View.GONE)
                liProgress.setVisibility(View.GONE);

        }
    }

}
