package com.wefly.wecollect.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.fxn.pix.Pix;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.Chip;
import com.wefly.wecollect.adapters.audioAdapter;
import com.wefly.wecollect.adapters.imageAdapter;
import com.wefly.wecollect.models.Alert;
import com.wefly.wecollect.models.Piece;
import com.wefly.wecollect.models.Recipient;
import com.wefly.wecollect.presenters.FormActivity;
import com.wefly.wecollect.tasks.CategoryGetTask;
import com.wefly.wecollect.utils.Constants;
import com.wefly.wecollect.utils.NetworkWatcher;
import com.wefly.wecollect.utils.PathUtil;
import com.wefly.wecollect.utils.design.AnimeView;
import com.weflyagri.wecollect.R;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

public class CreateAlertActivity extends FormActivity implements View.OnClickListener {

    static Map<String, Integer> response_category = new HashMap<>();
    protected Alert alert;
    View vForm, vImages, vAudio;
    RecyclerView recyclerView, audioRecyclerView;
    imageAdapter myAdapter;
    CopyOnWriteArrayList<Piece> pieces = new CopyOnWriteArrayList<>();
    Piece p = new Piece();
    ChipsInput ChipsAttachments;
    private EditText edObject, edContent;
    private RelativeLayout rlMain;
    private audioAdapter myAudioAdapter;
    private Spinner category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_alert);

        // Form fragment
        vForm = LayoutInflater.from(getBaseContext()).inflate(R.layout.fragment_create_alert_form, null, false);

        CategoryGetTask task = new CategoryGetTask(appController);
        try {
            response_category = task.execute().get();
            appController.setAlert_categories(response_category);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        category = vForm.findViewById(R.id.categorySpinner);

        //Load Category list in spinner
        List<String> category_list = new ArrayList<>();
        if (response_category != null) {
            for (Map.Entry entry : response_category.entrySet()) {
                category_list.add((String) entry.getKey());
            }
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, category_list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(spinnerAdapter);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        iniViews();
        iniViewAndColors();
        watcher = new NetworkWatcher(this, rlMain);
        iniListeners();
        iniChipInput();
    }

    @SuppressLint("RestrictedApi")
    private void iniViewAndColors() {
        bCancel = vForm.findViewById(R.id.btnCancel);
        bClose = vForm.findViewById(R.id.btnClose);
        bSend = vForm.findViewById(R.id.btnSend);

        rlMain = findViewById(R.id.Rlayout);
        liMain = null; // free memory in super

        butList.clear();
        butList.add(bCancel);
        butList.add(bSend);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // below lollipop
            ColorStateList csl = ColorStateList.valueOf(Color.TRANSPARENT);
            for (AppCompatImageButton btn : butList) {
                btn.setSupportBackgroundTintList(csl);
            }
        }
    }

    private void iniViews() {

        // images fragment
        vImages = LayoutInflater.from(getBaseContext()).inflate(R.layout.fragment_attachment, null, false);

        //Record fragment
        vAudio = LayoutInflater.from(getBaseContext()).inflate(R.layout.fragment_attachment_record, null, false);

        edObject = vForm.findViewById(R.id.objectEdText);
        edContent = vForm.findViewById(R.id.contentEdText);
        ciRecipients = vForm.findViewById(R.id.recipientsCi);


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
                return 3;
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
                    } else if (position == 1) {
                        // image fragment
                        view = vImages;
                    } else {
                        view = vAudio;
                    }
                    container.addView(view);

                } catch (Exception e) {
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

        //List recorded audio
        audioRecyclerView = vAudio.findViewById(R.id.AudiorecyclerView);
        audioRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAudioAdapter = new audioAdapter(this);
        audioRecyclerView.setAdapter(myAudioAdapter);
    }

    private void iniListeners() {
        for (AppCompatImageButton btn : butList) {
            btn.setOnClickListener(this);
        }
        bClose.setOnClickListener(v -> finish());

        vImages.findViewById(R.id.liSelectImage).setOnClickListener(view -> new AnimeView(view, view12 -> Pix.start(CreateAlertActivity.this, Constants.REQUEST_CODE_SELECT_IMAGES, Constants.MAX_SELECT_COUNT)).startAnimation());

        vAudio.findViewById(R.id.liSelectImage).setOnClickListener(view -> new AnimeView(view, view1 -> {
            Log.v("Audio recordbtn", "clicked");
            Intent recorder = new Intent(CreateAlertActivity.this, RecorderActivity.class);
            startActivityForResult(recorder, 352);
        }).startAnimation());
    }

    @Override
    public void onClick(View view) {
        saveInput();
        animMe(view, null, null, sAlert, this, watcher);
    }

    private void saveInput() throws NullPointerException {
        if (sAlert != null) {
            sAlert.setObject(edObject.getText().toString().trim());
            sAlert.setContent(edContent.getText().toString().trim());
            sAlert.setCategory((String) category.getSelectedItem().toString());
            recipientsSelected.clear();
            List<Recipient> list = (List<Recipient>) ciRecipients.getSelectedChipList();
            recipientsSelected.addAll(list);
            if (recipientsSelected.size() > 0)
                sAlert.setRecipients(recipientsSelected);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        ChipsAttachments = findViewById(R.id.chipAttachement);
        switch (requestCode) {
            case 100:
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
                            ChipsAttachments.addChip(p.getAvatarDrawable(), p.getLabel(), p.getExtension(p.getUrl()));
                            Log.v("Image URL", p.getUrl());

                            pieces.add(p);
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                    //Stockons les pieces dans l'appcontroller
                    appController.setPieceList(pieces);
                }
                break;
            case 352:
                if (resultCode == 200) {
                    //Fill the path
                    Piece audio = new Piece();
                    audio.setUrl(data.getExtras().getString("audioPath"));
                    audio.setContentUrl(Uri.fromFile(new File(audio.getUrl())));
                    Chip chip=new Chip(audio.getAvatarDrawable(),audio.getLabel(),audio.getExtension(audio.getUrl()));
                    if(!audio.equals(null))
                        ChipsAttachments = vForm.findViewById(R.id.chipAttachement);
                        ChipsAttachments.addChip(chip);
                    Log.v("Audio URL", audio.getUrl());

                    pieces.add(p);
                    myAudioAdapter.addAudio(p.getUrl());

                    for (Piece item : appController.getPieceList()) {
                        System.out.println("PIECE EXT" + item.getExtension(item.getUrl()));
                    }

                    //Log.v("PIECE SIZE 1", String.valueOf(pieces.size()));

                    appController.setPieceList(pieces);

                    Log.v("PIECE SIZE 1", String.valueOf(appController.getPieceList().size()));

                    if (audioRecyclerView.getVisibility() != View.VISIBLE)
                        audioRecyclerView.setVisibility(View.VISIBLE);
                    //Log.v("AUDIO List PATH", data.getExtras().getString("audioListPath"));
                } else {
                    Log.v("AUDIO PATH", "ERROR");
                }
                break;
        }

        Log.v("PIECE SIZE ", String.valueOf(pieces.size()));
    }

    @Override
    public void onSendSucces(@NonNull Alert s) {
        super.onSendSucces(s);
//        if (liMain != null)
//            Utils.showToast(this, R.string.email_sent, liMain);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
