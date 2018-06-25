package com.wefly.wecollect.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.fxn.pix.Pix;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.pchmn.materialchips.ChipsInput;
import com.wefly.wecollect.adapter.imageAdapter;
import com.wefly.wecollect.model.Email;
import com.wefly.wecollect.model.Piece;
import com.wefly.wecollect.model.Recipient;
import com.wefly.wecollect.model.Sms;
import com.wefly.wecollect.presenter.FormActivity;
import com.wefly.wecollect.task.PieceUploadTask;
import com.wefly.wecollect.utils.AppController;
import com.wefly.wecollect.utils.Constants;
import com.wefly.wecollect.utils.NetworkWatcher;
import com.wefly.wecollect.utils.PathUtil;
import com.wefly.wecollect.utils.Utils;
import com.wefly.wecollect.utils.design.AnimeView;
import com.weflyagri.wecollect.R;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CreateEmailActivity extends FormActivity implements View.OnClickListener {
    private EditText edObject, edContent;
    private LinearLayout liMain;
    View vForm, vImages;
    RecyclerView recyclerView;
    imageAdapter myAdapter;
    List<Piece> pieces = new CopyOnWriteArrayList<>();
    Piece p = new Piece();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_email);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        watcher = new NetworkWatcher(this, liMain);

        iniViews();
        iniViewAndColors();
        iniListeners();
        iniChipInput();
    }

    private void iniViews() {
        // Form fragment
        vForm = LayoutInflater
                .from(getBaseContext())
                .inflate(R.layout.fragment_create_email_from, null, false);

        // images fragment
        vImages = LayoutInflater
                .from(getBaseContext())
                .inflate(R.layout.fragment_attachment, null, false);

        edObject = vForm.findViewById(R.id.objectEdText);
        edContent = vForm.findViewById(R.id.contentEdText);
        ciRecipients = vForm.findViewById(R.id.recipientsCi);


        // Setup custom tab
        ViewGroup tab = findViewById(R.id.tab);
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
                    if (position == 0) {
                        // image fragment
                        view = vForm;
                    } else {
                        // image fragment
                        view = vImages;
                    }
                    container.addView(view);

                } catch (Exception e) {
                    e.printStackTrace();
                    View mView = LayoutInflater
                            .from(getBaseContext())
                            .inflate(R.layout.fragment_create_email_from, null, false);
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

    //initialization des listeners
    private void iniListeners() {
        for (AppCompatImageButton btn : butList) {
            btn.setOnClickListener(this);
        }
        //Lance l'activite de camera Pix
        vImages.findViewById(R.id.liSelectImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AnimeView(view, new AnimeView.OnAnimationEndCallBack() {
                    @Override
                    public void onEnd(@NonNull View view) {
                        Pix.start(CreateEmailActivity.this, Constants.REQUEST_CODE_SELECT_IMAGES, Constants.MAX_SELECT_COUNT);
                    }
                }).startAnimation();
            }
        });
    }

    private void iniViewAndColors() {
        bCancel = vForm.findViewById(R.id.btnCancel);
        bClose = vForm.findViewById(R.id.btnClose);
        bSend = vForm.findViewById(R.id.btnSend);

        liMain = findViewById(R.id.Rlayout);
        //liMain = null; // free memory in super

        butList.clear();
        butList.add(bCancel);
        butList.add(bClose);
        butList.add(bSend);


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // below lollipop
            ColorStateList csl = ColorStateList.valueOf(Color.TRANSPARENT);
            for (AppCompatImageButton btn : butList) {
                btn.setSupportBackgroundTintList(csl);
            }
        }
    }

    @Override
    public void onClick(View view) {
        Log.v("btn clicked", "true");
        saveInput();
        animMe(view, sEmail, null, null, this, watcher);
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

                    //On recupere la liste des URL des images
                    for (String r : returnValue) {
                        try {
                            p.setUrl(PathUtil.getPath(this, Uri.fromFile(new File(r))));
                            p.setContentUrl(Uri.fromFile(new File(r)));
                            Log.v("Image URI", p.getContentUrl().toString());
                            pieces.add(p);
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                    //Stockons les pieces dans l'appcontroller
                    appController.setPieceList(pieces);
                }
                break;
            }
        }
    }


    private void saveInput() {
        if (sEmail != null) {
            sEmail.setContent(edContent.getText().toString().trim());
            sEmail.setObject(edObject.getText().toString().trim());
            recipientsSelected.clear();
            List<Recipient> list = (List<Recipient>) ciRecipients.getSelectedChipList();
            for (Recipient mDm : list) {
                recipientsSelected.add(mDm);
            }

            //on ajoute les destinataires au sms
            if (recipientsSelected.size() > 0)
                sEmail.setRecipients(recipientsSelected);
        }
    }

    @Override
    public void onSendSucces(@NonNull Email s) {
        super.onSendSucces(s);
        if (liMain != null)
            Utils.showToast(this, R.string.email_sent, liMain);
        finish();
    }
}
