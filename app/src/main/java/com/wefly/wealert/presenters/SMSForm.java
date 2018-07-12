package com.wefly.wealert.presenters;

import android.widget.ImageButton;

import com.daimajia.numberprogressbar.OnProgressBarListener;
import com.wefly.wealert.models.Sms;

public class SMSForm extends DBActivity implements RecipientPresenter.OnRecipientDownloadCallBack, OnProgressBarListener, DialogPresenter.OnDialogListener {

    protected ImageButton bClose, bSend, bCancel;
    protected Sms sSms = new Sms();

    @Override
    public void onProgressChange(int current, int max) {

    }

    @Override
    public void onSaveRequest() {

    }

    @Override
    public void onDeleteRequest() {

    }

    @Override
    public void onRecipientDownloadSucces() {

    }
}
