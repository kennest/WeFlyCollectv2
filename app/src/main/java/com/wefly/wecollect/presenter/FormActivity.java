package com.wefly.wecollect.presenter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.daimajia.numberprogressbar.OnProgressBarListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.OnCancelListener;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.pchmn.materialchips.ChipsInput;
import com.wefly.wecollect.model.Alert;
import com.wefly.wecollect.model.Email;
import com.wefly.wecollect.model.Piece;
import com.wefly.wecollect.model.Recipient;
import com.wefly.wecollect.model.Sms;
import com.wefly.wecollect.utils.Constants;
import com.wefly.wecollect.utils.NetworkWatcher;
import com.wefly.wecollect.utils.Utils;
import com.weflyagri.wecollect.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by admin on 06/06/2018.
 */

public class FormActivity extends DBActivity implements RecipientPresenter.OnRecipientDownloadCallBack , OnProgressBarListener, DialogPresenter.OnDialogListener{
    protected ArrayList<AppCompatImageButton> butList = new ArrayList<>();
    protected AppCompatImageButton bClose, bSend, bCancel;
    protected LinearLayout liMain, liLoading, liProgress;
    protected ScrollView srvMain;
    protected Email sEmail = new Email();
    protected Sms sSms =  new Sms();
    protected Alert sAlert = new Alert();
    protected int selected = 0;
    protected ChipsInput ciRecipients;
    protected CopyOnWriteArrayList<Recipient> recipientsList;
    protected CopyOnWriteArrayList<Recipient> recipientsSelected = new CopyOnWriteArrayList<>();
    protected NumberProgressBar bnp;
    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void iniViewColors() {
        bCancel         = (AppCompatImageButton) findViewById(R.id.btnCancel);
        bClose          = (AppCompatImageButton) findViewById(R.id.btnClose);
        bSend           = (AppCompatImageButton) findViewById(R.id.btnSend);
        bnp             = (NumberProgressBar)findViewById(R.id.numberbar1);
        liMain          = (LinearLayout)         findViewById(R.id.liMain);
        liLoading       = (LinearLayout)         findViewById(R.id.liLoadingNum);
        liProgress      = (LinearLayout)         findViewById(R.id.liLoading);
        srvMain         = (ScrollView)           findViewById(R.id.srvMain);
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
        bnp.setOnProgressBarListener(this);
    }

    @Override
    public void onProgressChange(int current, int max) {

    }

    @Override
    public void onSaveRequest() {

    }

    @Override
    public void onDeleteRequest() {

    }

    protected class CycleInterpolator implements android.view.animation.Interpolator {

        private final float mCycles = 0.5f;

        @Override
        public float getInterpolation(final float input) {
            return (float) Math.sin(2.0f * mCycles * Math.PI * input);
        }
    }


    protected void animMe(@NonNull final View v, @Nullable final Email email, @Nullable final Sms sms, @Nullable final Alert alert,  @NonNull final DBActivity act, @NonNull final NetworkWatcher w){
        ViewCompat.animate(v)
                .setDuration(200)
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setInterpolator(new CycleInterpolator())
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(final View view) {

                    }

                    @Override
                    public void onAnimationEnd(final View view) {
                        switch (view.getId()){

                            case R.id.btnSend:
                                if (email != null){
                                    if (email.getRecipients().size() == 0){
                                        // No recipients
                                        showMessage(R.string.empty_recipient);
                                    }else if(email.getContent().equals("")){
                                        // empty content
                                        showMessage(R.string.empty_content);
                                    }else if(email.getObject().isEmpty()){
                                        showMessage(R.string.empty_object);
                                    }
                                    else {
                                        onSelected(3, w);
                                    }
                                }
                                else if (sms != null){
                                    if (sms.getRecipients().size() == 0){
                                        // No recipients
                                        showMessage(R.string.empty_recipient);
                                    }else if(sms.getContent().equals("")){
                                        // empty content
                                        showMessage(R.string.empty_content);
                                    }else {
                                        onSelected(2, w);
                                    }

                                }
                                else if (alert != null){
                                    onSelected(1, w);
                                }


                                break;
                            case R.id.btnCancel: // DISMISS
                                if (email != null){
                                    // SAVE EMAIL
                                } else if (sms != null){
                                    // SAVE SMS
                                    requestSave(act, sms,null,null);
                                } else if (alert != null){
                                    // SAVE ALERT
                                }
                                break;
                            case R.id.btnClose:
                                act.finish();
                                break;
                            default:
                                break;
                        }

                    }

                    @Override
                    public void onAnimationCancel(final View view) {

                    }
                })
                .withLayer()
                .start();
    }

    private void requestSave(@NonNull final DBActivity act, @Nullable final Sms sms,@Nullable Alert alert,@Nullable Email email) {
        if (sms.getRecipients().size() > 0 || !sms.getContent().equals("")){
            showDialog(null, sms, null);
        }else
            act.finish();
    }

    private void onSelected(int i, @NonNull  NetworkWatcher wat) {
        Log.v(Constants.APP_NAME, TAG + " onSelected RUN");
        selected = i;
        wat.setOnInternetListener(this);
        checkTokenAndNetwork(this,wat);
    }


    @Override
    public void onConnected() {
        super.onConnected();
        Log.v(Constants.APP_NAME, TAG + " onConnected selected" + selected +" RUN");
        switch (selected){
            case 1:
                // ALERT
                if (sAlert != null){
                    showMessage(R.string.sending);
                    super.sendAlert(sAlert);
                    super.uploadPiece(null, sAlert);
                }
                break;
            case 2:
                // SMS

                if (sSms != null){
                    Log.v(Constants.APP_NAME, TAG + " SMS NOT NULL  RUN");
                    lockSendBtn();
                    showMessage(R.string.sending);
                    super.sendSms(sSms, this);
                }else
                    Log.v(Constants.APP_NAME, TAG + " SMS IS NULL  RUN");

                break;
            case 3:
                //Email
                if (sEmail != null){
                    showMessage(R.string.sending);
                    super.sendEmail(sEmail);
                    super.uploadPiece(sEmail, null);
                }

                break;
            default:
                break;
        }
        selected = 0;
        Log.v(Constants.APP_NAME, TAG + " onConnected selected" + selected +" RUN");
    }

    protected void lockSendBtn() {
        if (bSend != null){
            bSend.setClickable(false);
        }
    }

    protected void unlockSendBtn(){
        if (bSend != null){
            bSend.setClickable(true);
        }
    }

    protected void showMessage(@StringRes int msg ) {
        if (liMain != null)
            Utils.showToast(this, msg, liMain);
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

    @Override
    public void onSendError(@NonNull Sms s) {
        super.onSendError(s);
        unlockSendBtn();
    }

    @Override
    public void onSendErrorNetwork(@NonNull Sms s) {
        super.onSendErrorNetwork(s);
        unlockSendBtn();
    }

    @Override
    public void onSendSucces(@NonNull Sms s) {
        super.onSendSucces(s);
        unlockSendBtn();
    }


    //Remplis les Chips Inputs
    protected void iniChipInput(){
        // get Recipients
        Log.d("INIT CHIP INPUT","OK");
        RecipientPresenter p = new RecipientPresenter(this);
        if (recipientsList == null || recipientsList.size() == 0){
            Log.d("INIT CHIP INPUT","LIST NULL OR E:PTY");
            if (liMain != null)
                p.setOnRecipientDownloadCallBack(this);
            recipientsList = p.getRecipients(); // Auto download if empty
        }
        // FIX: UnsupportedOperationException
        try {
            ArrayList<Recipient> rList = new ArrayList<>();
            rList.addAll(recipientsList);
            Log.d("INIT CHIP INPUT","LIST addAll Run");

            if (rList.size() > 0){
                if (ciRecipients != null){
                    Log.d("INIT CHIP INPUT","LIST ciRecipients NOT NULL Run");
                    ciRecipients.setFilterableList(rList);
                }else{
                    Log.d("INIT CHIP INPUT","LIST ciRecipients IS NULL Run");
                }


            }else{
                Log.d("INIT CHIP INPUT","LIST ciRecipients IS EMPTY Run");
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.d("INIT CHIP INPUT","CHIP UnsupportedOperationException");
        }
    }

    @Override
    public void onRecipientDownloadSucces() {
        iniChipInput();
    }

    protected void counterResetAndHide(){
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                counterHide();
                                bnp.setProgress(0);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }, 800);

    }

    protected void counterHide(){
        try {
            liLoading.setVisibility(View.GONE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void notifCounter(int p){
        if (liLoading != null && liLoading.getVisibility() != View.VISIBLE)
            liLoading.setVisibility(View.VISIBLE);
        if (bnp != null){
            bnp.setProgress(p);

            if (p == 0)
                counterHide();

            if (p == bnp.getMax())
                counterResetAndHide();
        }

    }

    protected void setCounterMax(int max){
        if (bnp != null){
            bnp.setMax(max);
        }

    }

    protected @Nullable NumberProgressBar getCounter(){
        return bnp;
    }

    protected void showDialog( @Nullable final Email email, @Nullable final Sms sms, @Nullable final Alert alert){
        final DialogPresenter pre = new DialogPresenter();
        pre.setOnDialogListener(this);
        Holder holder = new ViewHolder(R.layout.content);
        DialogPlus dialog = DialogPlus.newDialog(this)
                .setContentHolder(holder)
                .setHeader(R.layout.header)
                .setCancelable(true)
                .setGravity(Gravity.BOTTOM)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialogPlus, View v) {
                        switch (v.getId()){
                            case R.id.noButton:
                                pre.shouldNotify(false);
                                dialogPlus.dismiss();
                                break;

                            case R.id.yesButton:
                                // Can Save
                                if (email != null){
                                    // SAVE EMAIL
                                }
                                else if (sms != null){
                                    // SAVE SMS
                                    pre.shouldNotify(true);

                                }
                                else if (alert != null){
                                    // SAVE ALERT
                                }

                                dialogPlus.dismiss();
                            default:
                                break;

                        }
                    }
                })
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override public void onItemClick(DialogPlus dialog, Object item, View v, int position) {

                    }
                })
                .setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogPlus dialogPlus) {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel(DialogPlus dialogPlus) {

                    }
                })
                .create();
//        Button btnNo = (Button) dialog.findViewById(R.id.noButton);
//        Button btnYes = (Button) dialog.findViewById(R.id.yesButton);
//        TextView msg = (TextView) dialog.findViewById(R.id.msg);
//        TextView title = (TextView) dialog.findViewById(R.id.title);
//        title.setText(getString(R.string.permission_alert_title));
//        msg.setText(getString(R.string.alert_msg_granted));
//        btnNo.setText(ctx.getString(R.string.no));
//        btnYes.setText(ctx.getString(R.string.yes));

        dialog.show();
    }

}
