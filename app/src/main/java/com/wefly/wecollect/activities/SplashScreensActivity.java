package com.wefly.wecollect.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.wefly.wecollect.MainActivity;
import com.wefly.wecollect.presenters.FormActivity;
import com.wefly.wecollect.presenters.RecipientPresenter;
import com.wefly.wecollect.utils.Constants;
import com.wefly.wecollect.utils.NetworkWatcher;
import com.wefly.wecollect.utils.PermissionUtil;
import com.weflyagri.wecollect.R;

import java.util.concurrent.TimeUnit;

public class SplashScreensActivity extends FormActivity {
    private static boolean isAllPermissionGranted = false;
    private static boolean showPermissionDialog;
    private static boolean isFirstTime = true;
    private PermissionUtil pUtil;
    private RelativeLayout rLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        pUtil = new PermissionUtil(this);
        rLayout = (RelativeLayout) findViewById(R.id.Rlayout);
        watcher = new NetworkWatcher(this, rLayout);


        //check permission
        isAllPermissionGranted = pUtil.isAllPermissionsGranded();
        if (!isAllPermissionGranted) {
            if (isFirstTime) {
                showPermissionDialog = false;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            onRequestAllPermissions();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, TimeUnit.SECONDS.toMillis(1));
                isFirstTime = false;
            }
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //check if User is already connected
                    //Read token
                    try {
                        //start session
                        startSession();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 4000);
        }


    }


    public void onRequestAllPermissions() {
        //request Permission
        pUtil.requestAllPermissions();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (showPermissionDialog)
            pUtil.requestAllPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_APP_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                    showPermissionDialog = false;
                    isAllPermissionGranted = true;

                    // Check user Token
                    try {
                        //start session
                        startSession();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    showPermissionDialog = true;
                    // Display on Next launch
                    isFirstTime = true;
                    try {
                        pUtil.onAllPermissionError(pUtil.getPermissionsDenied());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void startSession() {
        if (appController != null) {
            if (appController.isTokenValide()) {
                if (watcher != null && rLayout != null) {
                    watcher.setOnOffLineListener(new NetworkWatcher.OnOffLineListener() {
                        @Override
                        public void onOffLine() {
                            startActivity(new Intent(SplashScreensActivity.this, MainActivity.class));
                            finish();
                        }

                        @Override
                        public void onOnLine() {
                            // RECIPIENTS
                            RecipientPresenter presenter = new RecipientPresenter(SplashScreensActivity.this);
                            presenter.setOnRecipientDownloadCallBack(new RecipientPresenter.OnRecipientDownloadCallBack() {
                                @Override
                                public void onRecipientDownloadSucces() {

                                }
                            });
                            presenter.downloadAllRecipients(false);
                            startActivity(new Intent(SplashScreensActivity.this, LoadingActivity.class));
                            finish();
                        }
                    });
                    watcher.checkOffLine();
                }

            } else {
                startActivity(new Intent(SplashScreensActivity.this, LoginActivity.class));
                finish();
            }

        }
    }
}
