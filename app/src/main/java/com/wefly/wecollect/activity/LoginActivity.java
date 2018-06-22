package com.wefly.wecollect.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nightonke.boommenu.Util;
import com.wefly.wecollect.MainActivity;
import com.wefly.wecollect.presenter.BaseActivity;
import com.wefly.wecollect.task.LoginTask;
import com.wefly.wecollect.utils.AppController;
import com.wefly.wecollect.utils.Constants;
import com.wefly.wecollect.utils.Save;
import com.wefly.wecollect.utils.Utils;
import com.wefly.wecollect.utils.design.AnimeView;
import com.weflyagri.wecollect.R;

 public class LoginActivity extends BaseActivity implements LoginTask.OnLoginListener{
    private AppCompatImageButton login;
    private AnimeView aView;
    private RelativeLayout rlMain;
    private LinearLayout liMain;
    private LinearLayout liLoading;
    private EditText edName, edPassword;
    private String name = "";
    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        iniView();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // below lollipop
            ColorStateList csl = ColorStateList.valueOf(Color.TRANSPARENT);
            login.setSupportBackgroundTintList(csl);

        }

        iniListener();
        AppController.addToDestroyList(this);

        try {
            String name = Save.defaultLoadString(Constants.PREF_USER_NAME, this);
            String pwd = Save.defaultLoadString(Constants.PREF_USER_PASSWORD, this);

            if (name != null && pwd!= null){
                edName.setText(name);
                edPassword.setText(pwd);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void iniListener() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                aView = new AnimeView(login, new AnimeView.OnAnimationEndCallBack() {
                    @Override
                    public void onEnd(@NonNull View view) {
//                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                        finish();
                        name            = edName.getText().toString().trim();
                        password        = edPassword.getText().toString().trim();

                        if (isInputValid()){
                            if (appController != null){
                                if (appController.isNetworkAvailable()){
                                    onDisplayUI(false, true, false, false);
                                    LoginTask task = new LoginTask(name, password, LoginActivity.this);
                                    task.setOnLoginListener(LoginActivity.this, rlMain);
                                    task.execute();
                                }else
                                    Utils.showToast(LoginActivity.this, R.string.error_no_internet, rlMain);

                            }
                        }

                    }
                });
                aView.startAnimation();
            }
        });


    }

    private boolean isInputValid() {
        if (name.equals("")){
            Utils.showToast(this,R.string.name_empty , rlMain);
            return false;
        }

        if (password.equals("")){
            Utils.showToast(this,R.string.password_empty , rlMain);
            return false;
        }

        return true;

    }

    private void iniView() {
        login               = (AppCompatImageButton) findViewById(R.id.loginBtn);

        rlMain              = (RelativeLayout)       findViewById(R.id.Rlayout);

        liMain              = (LinearLayout)         findViewById(R.id.liMain);
        liLoading           = (LinearLayout)         findViewById(R.id.liLoading);

        edName              = (EditText)             findViewById(R.id.nameEdText);
        edPassword          = (EditText)             findViewById(R.id.passwordEdText);
    }

     private void onDisplayUI(boolean canDisplayUI, boolean canDisableBtn, boolean isLoginError, boolean isServerError) {
        if (rlMain != null & login != null){
            if (canDisableBtn){
                login.setClickable(false);
            }else
                login.setClickable(true);

            if (isLoginError)
                Utils.showToast(this, R.string.error_login, rlMain);

            if (canDisplayUI){
                liMain.setVisibility(View.VISIBLE);
                liLoading.setVisibility(View.GONE);
            }else{
                liMain.setVisibility(View.INVISIBLE);
                liLoading.setVisibility(View.VISIBLE);
            }

            if (isServerError){
                Utils.showToast(this, R.string.error_api, rlMain);
            }

        }

     }


     @Override
    public void onLoginError(@NonNull View view) {
        onDisplayUI(true, false, true, false);
    }

     @Override
     public void onLoginNetworkError() {
        onDisplayUI(true,false, false, true);
     }

     @Override
    public void onLoginSucces() {
        onDisplayUI(true, false, false, false);
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();

    }

     @Override
     protected void onSaveInstanceState(Bundle outState) {
        outState.putString(Constants.STATE_USER_NAME,name );
        outState.putString(Constants.STATE_USER_NAME,password );
         super.onSaveInstanceState(outState);
     }

     @Override
     protected void onRestoreInstanceState(Bundle savedInstanceState) {
         super.onRestoreInstanceState(savedInstanceState);
         // restore old State
         String uName           = savedInstanceState.getString(Constants.STATE_USER_NAME);
         String uPassword       = savedInstanceState.getString(Constants.STATE_USER_PASSWORD);

         if (uName != null & uPassword != null & edPassword != null& edName != null ){
             edName.setText(uName);
             edPassword.setText(uPassword);
         }
     }
}
