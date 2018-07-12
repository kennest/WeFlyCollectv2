package com.wefly.wealert;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.OnBoomListenerAdapter;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.wefly.wealert.activities.BootActivity;
import com.wefly.wealert.activities.CreateAlertActivity;
import com.wefly.wealert.activities.CreateEmailActivity;
import com.wefly.wealert.activities.CreateSMSActivity;
import com.wefly.wealert.activities.LoginActivity;
import com.wefly.wealert.adapters.AlertAdapter;
import com.wefly.wealert.adapters.EmailAdapter;
import com.wefly.wealert.models.Alert;
import com.wefly.wealert.models.Email;
import com.wefly.wealert.presenters.DBActivity;
import com.wefly.wealert.tasks.AlertReceiveGetTask;
import com.wefly.wealert.tasks.EmailReceiveGetTask;
import com.wefly.wealert.utils.AppController;
import com.wefly.wealert.utils.BuilderManager;
import com.wefly.wealert.utils.Constants;
import com.weflyagri.wealert.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import devlight.io.library.ntb.NavigationTabBar;


public class MainActivity extends DBActivity {
    private BoomMenuButton bmb;
    private int selected;
    private Button recordbtn;
    private RelativeLayout rLayout;
    View vEmail, vSms, vAlert;
    View vsSent, vsReceive, vsDraft;
    List<Email> emailList = new ArrayList<>();
    List<Alert> alertList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rLayout = findViewById(R.id.Rlayout);

        // Email fragment
        vEmail = LayoutInflater.from(
                getBaseContext()).inflate(R.layout.fragment_email, null, false);

        //On charge la liste des Emails
        loadEmailList(vEmail);

        // Sms fragment
        vSms = LayoutInflater.from(
                getBaseContext()).inflate(R.layout.fragment_sms, null, false);

        // Alert fragment
        vAlert = LayoutInflater.from(
                getBaseContext()).inflate(R.layout.fragment_alert, null, false);

        loadAlertList(vAlert);
        // SMS -> sent
        vsSent = LayoutInflater.from(
                getBaseContext()).inflate(R.layout.fragment_sms_sent, null, false);

        vsReceive = LayoutInflater.from(
                getBaseContext()).inflate(R.layout.fragment_sms_receive, null, false);

        vsDraft = LayoutInflater.from(
                getBaseContext()).inflate(R.layout.fragment_sms_draft, null, false);


        // Setup custom tab
        ViewGroup tab = findViewById(R.id.tab);
        tab.addView(LayoutInflater.from(this).inflate(R.layout.fragment_main, tab, false));

        ViewPager viewPager = findViewById(R.id.viewpager);
        SmartTabLayout viewPagerTab = findViewById(R.id.viewpagertab);
        if (appController != null)
            appController.setup(viewPagerTab);


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
                    switch (position) {
                        case 0:
                            //EMAIL fragment
                            view = vEmail;
                            break;
                        case 1:
                            //SMS
                            view = vSms;
                            break;
                        case 2:
                            //ALERT
                            view = vAlert;
                            break;
                        default:
                            view = vEmail;
                            // do something
                            break;
                    }

                    container.addView(view);
                    return view;

                } catch (Exception e) {
                    e.printStackTrace();
                    // Details
                    View mView = LayoutInflater.from(
                            getBaseContext()).inflate(R.layout.fragment_email, null, false);
                    container.addView(mView);
                    return mView;
                }

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

        iniBoomButton();
        initTabSms();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == 10) {
                Log.v("Record OK", "OK");
            } else {
                // Oops! User has canceled the recording
                Log.v("Record OK", "No");
            }
        }
    }


    //initialize BoomButton
    private void iniBoomButton() {
        bmb = findViewById(R.id.bmb);
        assert bmb != null;

        bmb.addBuilder(BuilderManager.getHamButtonBuilder(getString(R.string.send_sms), "")
                .normalImageDrawable(new IconicsDrawable(this, FontAwesome.Icon.faw_mobile_alt)
                        .color(ContextCompat.getColor(this, R.color.white))
                        .paddingDp(Constants.DRAWER_ICON_PADDING)
                        .sizeDp(Constants.DRAWER_ICON_BIG_SIZE)));

        bmb.addBuilder(BuilderManager.getHamButtonBuilder(getString(R.string.send_email), "")
                .normalImageDrawable(new IconicsDrawable(this, FontAwesome.Icon.faw_envelope)
                        .color(ContextCompat.getColor(this, R.color.white))
                        .paddingDp(Constants.DRAWER_ICON_PADDING)
                        .sizeDp(Constants.DRAWER_ICON_BIG_SIZE)));

        bmb.addBuilder(BuilderManager.getHamButtonBuilder(getString(R.string.send_alert), "")
                .normalImageDrawable(new IconicsDrawable(this, FontAwesome.Icon.faw_exclamation_triangle)
                        .color(ContextCompat.getColor(this, R.color.white))
                        .paddingDp(Constants.DRAWER_ICON_PADDING)
                        .sizeDp(Constants.DRAWER_ICON_BIG_SIZE)));

        bmb.addBuilder(BuilderManager.getHamButtonBuilder(getString(R.string.account), "")
                .normalImageDrawable(new IconicsDrawable(this, FontAwesome.Icon.faw_user)
                        .color(ContextCompat.getColor(this, R.color.white))
                        .paddingDp(Constants.DRAWER_ICON_PADDING)
                        .sizeDp(Constants.DRAWER_ICON_BIG_SIZE)));

        bmb.addBuilder(BuilderManager.getHamButtonBuilder(getString(R.string.exit), "")
                .normalImageDrawable(new IconicsDrawable(this, FontAwesome.Icon.faw_times_circle)
                        .color(ContextCompat.getColor(this, R.color.white))
                        .paddingDp(Constants.DRAWER_ICON_PADDING)
                        .sizeDp(Constants.DRAWER_ICON_BIG_SIZE)));

        bmb.setOnBoomListener(new OnBoomListenerAdapter() {
            @Override
            public void onClicked(int index, BoomButton boomButton) {
                super.onClicked(index, boomButton);

                switch (index) {
                    case 0:
                        // Sender Sms
                        startActivity(new Intent(MainActivity.this, CreateSMSActivity.class));
                        break;
                    case 1:
                        // EMAIL
                        startActivity(new Intent(MainActivity.this, CreateEmailActivity.class));
                        break;
                    case 2:
                        //ALERT
                        startActivity(new Intent(MainActivity.this, CreateAlertActivity.class));
                        break;
                    case 3:
                        //PROFIL

                        startActivity(new Intent(MainActivity.this, BootActivity.class));
                        break;
                    case 4:
                        //EXIT
                        AppController.clearAsynTask();
                        AppController.clearDestroyList();
                        finishAffinity();
                        System.exit(0);
                        break;
                    default:
                        break;
                }
                // Hide menu

            }
        });
    }

//    private void changeBoomButton(int index) {
//        // From version 2.0.9, BMB supports a new feature to change contents in boom-button
//        // by changing contents in the corresponding builder.
//        // Please notice that not every method supports this feature. Only the method whose comment
//        // contains the "Synchronicity" tag supports.
//        // For more details, check:
//        // https://github.com/Nightonke/BoomMenu/wiki/Change-Boom-Buttons-Dynamically
//        HamButton.Builder builder = (HamButton.Builder) bmb.getBuilder(index);
//        if (index == 0) {
//            builder.normalText("Changed!");
//            builder.highlightedText("Highlighted, changed!");
//            builder.subNormalText("Sub-text, changed!");
//            builder.normalTextColor(Color.YELLOW);
//            builder.highlightedTextColorRes(R.color.colorPrimary);
//            builder.subNormalTextColor(Color.BLACK);
//        } else if (index == 1) {
//            builder.normalImageRes(R.drawable.ic_login);
//            builder.highlightedImageRes(R.drawable.bear);
//        } else if (index == 2) {
//            builder.normalColorRes(R.color.colorAccent);
//        } else if (index == 3) {
//            builder.pieceColor(Color.WHITE);
//        } else if (index == 4) {
//            builder.unable(true);
//        }
//    }

    protected void loadAlertList(View v){
        //On charge la page alert avec les alerts
        try {
            alertList = new AlertReceiveGetTask(appController).execute().get();
            Log.v(getPackageName() + "emailList Size", String.valueOf(alertList.size()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        ListView emailListView = v.findViewById(R.id.alertListView);

        if (alertList.size() > 0) {
            emailListView.setAdapter(new AlertAdapter(getApplicationContext(), alertList));
        }
    }

    protected void loadEmailList(View v) {

        //On charge la page email avec les emails
        try {
            emailList = new EmailReceiveGetTask(appController).execute().get();
            Log.v(getPackageName() + "emailList Size", String.valueOf(emailList.size()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        ListView emailListView = v.findViewById(R.id.emailListView);

        if (emailList.size() > 0) {
            emailListView.setAdapter(new EmailAdapter(getApplicationContext(), emailList));
        }
    }

    @Override
    public void onConnected() {
        super.onConnected();

        switch (selected) {

            case 1: // 1 is undefine

                break;
            default:
                break;
        }
        selected = 0;


    }

    @Override
    public void onNotConnected() {
        super.onNotConnected();
    }

    @Override
    public void onRetry() {
        super.onRetry();
        if (watcher != null)
            watcher.isNetworkAvailable();
    }


    private void initTabSms() {
        final ViewPager viewPager = vSms.findViewById(R.id.vp_horizontal_ntb);
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

                    switch (position) {
                        case 1:
                            // SMS sent
                            view = vsSent;
                            break;
                        case 2:
                            // Sms receive
                            view = vsReceive;
                            break;
                        case 3:
                            // Sms draft
                            view = vsDraft;
                            break;

                        default:
                            view = vsSent; // do something
                            break;
                    }

                    container.addView(view);
                    return view;

                } catch (Exception e) {
                    e.printStackTrace();
                    // Details
                    View mView = LayoutInflater.from(
                            getBaseContext()).inflate(R.layout.fragment_sms_sent, null, false);
                    container.addView(mView);
                    return mView;
                }

            }
        });

        final NavigationTabBar navigationTabBar = vSms.findViewById(R.id.ntb_horizontal);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();

        models.add(
                new NavigationTabBar.Model.Builder(
                        new IconicsDrawable(this, FontAwesome.Icon.faw_chevron_down)
                                .color(ContextCompat.getColor(this, R.color.white))
                                .sizeDp(Constants.HAMBURGER_ICON_SIZE),
                        ContextCompat.getColor(this, R.color.material_orange_700))
                        .selectedIcon(
                                new IconicsDrawable(this, FontAwesome.Icon.faw_download)
                                        .color(ContextCompat.getColor(this, R.color.white))
                                        .sizeDp(Constants.HAMBURGER_ICON_SIZE)
                        )
                        .title("Reçu(s)")
                        .badgeTitle("")
                        .build()
        );

        models.add(
                new NavigationTabBar.Model.Builder(
                        new IconicsDrawable(this, FontAwesome.Icon.faw_cloud_upload_alt)
                                .color(ContextCompat.getColor(this, R.color.white))
                                .sizeDp(Constants.HAMBURGER_ICON_SIZE),
                        ContextCompat.getColor(this, R.color.material_lime_700))
                        .selectedIcon(
                                new IconicsDrawable(this, FontAwesome.Icon.faw_upload)
                                        .color(ContextCompat.getColor(this, R.color.white))
                                        .sizeDp(Constants.HAMBURGER_ICON_SIZE)
                        )
                        .title("Envoyé(s)")
                        .badgeTitle("")
                        .build()
        );

        models.add(
                new NavigationTabBar.Model.Builder(
                        new IconicsDrawable(this, FontAwesome.Icon.faw_save)
                                .color(ContextCompat.getColor(this, R.color.white))
                                .sizeDp(Constants.HAMBURGER_ICON_SIZE),
                        ContextCompat.getColor(this, R.color.material_lime_700))
                        .selectedIcon(
                                new IconicsDrawable(this, FontAwesome.Icon.faw_save2)
                                        .color(ContextCompat.getColor(this, R.color.white))
                                        .sizeDp(Constants.HAMBURGER_ICON_SIZE)
                        )
                        .title("Brouillon")
                        .badgeTitle("")
                        .build()
        );

        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 1);
        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                navigationTabBar.getModels().get(position).hideBadge();
            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });
    }
}
