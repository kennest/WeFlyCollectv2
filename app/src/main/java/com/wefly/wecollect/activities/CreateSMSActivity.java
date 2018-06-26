package com.wefly.wecollect.activities;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.wefly.wecollect.models.Recipient;
import com.wefly.wecollect.models.Sms;
import com.wefly.wecollect.presenters.FormActivity;
import com.wefly.wecollect.utils.Constants;
import com.wefly.wecollect.utils.NetworkWatcher;
import com.wefly.wecollect.utils.Utils;
import com.weflyagri.wecollect.R;

import java.util.List;

public class CreateSMSActivity extends FormActivity implements View.OnClickListener {

    private EditText edContent;
    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sms);

        super.iniViewColors();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        watcher = new NetworkWatcher(this, liMain);

        iniListeners();
        iniViews();
        iniChipInput();
    }

    private void iniViews() {
        edContent = findViewById(R.id.contentEdText);
        ciRecipients = findViewById(R.id.recipientsCi);
    }

    private void iniListeners() {
        for (AppCompatImageButton btn : butList) {
            btn.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        saveInput();
        animMe(view, null, sSms, null, this, watcher);
    }


    private void saveInput() {
        if (sSms != null) {
            sSms.setContent(edContent.getText().toString().trim());
            recipientsSelected.clear();
            List<Recipient> list = (List<Recipient>) ciRecipients.getSelectedChipList();
            for (Recipient mDm : list) {
                recipientsSelected.add(mDm);
            }

            //on ajoute les destinataires au sms
            if (recipientsSelected.size() > 0)
                sSms.setRecipients(recipientsSelected);
        }
    }

    @Override
    public void onSendError(@NonNull Sms s) {
        super.onSendError(s);
        if (liMain != null)
            Utils.showToast(this, R.string.sms_send_error, liMain);
    }

    @Override
    public void onSendSucces(@NonNull Sms s) {
        super.onSendSucces(s);
        try {
            if (liMain != null)
                Utils.showToast(this, R.string.sms_sent, liMain);
            onDisplayUi(sSms, false, false, false);
            Thread.sleep(5000);
            finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected() {
        super.onConnected();
    }

    @Override
    public void onNotConnected() {
        super.onNotConnected();
    }

    @Override
    public void onRetry() {
        super.onRetry();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        // Prepare Save
        if (ciRecipients != null && edContent != null) {
            recipientsSelected.clear();
            Sms smsToSave = new Sms();
            List<Recipient> list = (List<Recipient>) ciRecipients.getSelectedChipList();
            for (Recipient mDm : list) {
                recipientsSelected.add(mDm);
            }

            if (recipientsSelected.size() > 0)
                smsToSave.setRecipients(recipientsSelected);

            smsToSave.setContent(edContent.getText().toString().trim());
            outState.putSerializable(Constants.STATE_SMS, smsToSave);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // restore old State
        sSms = null;
        sSms = (Sms) savedInstanceState.getSerializable(Constants.STATE_SMS);
        if (sSms != null)
            onDisplayUi(sSms, true, false, false);
    }

    private void onDisplayUi(@NonNull Sms sms, boolean isRestoreState, boolean isSaving, boolean isSending) {

        if (isRestoreState) {
            if (edContent != null && ciRecipients != null) {
                edContent.setText(sms.getContent());
                //Existing Sms
                // old Selected recipients
                if (sms.getRecipients().size() > 0) {
                    for (Recipient dm : sms.getRecipients()) {
                        ciRecipients.addChip(dm);
                    }
                }
            }
        }

        if (isSaving) {
            if (srvMain != null && liProgress != null) {
                TextView textView = liProgress.findViewById(R.id.loadingTitleTView);
                if (textView != null)
                    textView.setText(R.string.saving);
                srvMain.setVisibility(View.INVISIBLE);
                liProgress.setVisibility(View.VISIBLE);
            }
        } else {
            showUI();
        }

        if (isSending) {
            if (srvMain != null && liProgress != null) {
                srvMain.setVisibility(View.INVISIBLE);
                liProgress.setVisibility(View.VISIBLE);
            }
        } else {
            showUI();
        }
    }

    private void showUI() {
        if (srvMain != null && liProgress != null) {
            if (srvMain.getVisibility() != View.VISIBLE)
                srvMain.setVisibility(View.VISIBLE);
            if (liProgress.getVisibility() != View.GONE)
                liProgress.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveError(@NonNull Sms s) {
        super.onSaveError(s);
        if (bCancel != null)
            bCancel.setClickable(true);
        onDisplayUi(sSms, false, false, false);

        if (liMain != null)
            Utils.showToast(this, R.string.save_error, liMain);
    }

    @Override
    public void onSaveSucces(@NonNull Sms s) {
        super.onSaveSucces(s);
        if (bCancel != null)
            bCancel.setClickable(true);
        onDisplayUi(sSms, false, false, false);

        if (liMain != null)
            Utils.showToast(CreateSMSActivity.this, R.string.save_ok, liMain);

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            exist();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 300);
    }

    @Override
    public void onSaveRequest() {
        super.onSaveRequest();
        // can save
        if (bCancel != null)
            bCancel.setClickable(false);
        onDisplayUi(sSms, false, true, false);
        super.addSms(this, sSms);
    }

    @Override
    public void onDeleteRequest() {
        super.onDeleteRequest();
        // don't save
        exist();

    }

    private void exist() {
        finish();
    }

    @Override
    public void onBackPressed() {
        saveInput();
        if (sSms != null) {
            if (sSms.getRecipients().size() > 0 || !sSms.getContent().equals("")) {
                showDialog(null, sSms, null);
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }


    }


}
