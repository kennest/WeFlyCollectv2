package com.wefly.wecollect.presenters;

import android.support.annotation.NonNull;
import android.view.View;

import com.wefly.wecollect.models.Sms;
import com.wefly.wecollect.tasks.SmsReceiveGetTask;
import com.wefly.wecollect.tasks.SmsSentGetTask;
import com.wefly.wecollect.utils.AppController;
import com.wefly.wecollect.utils.Constants;
import com.wefly.wecollect.utils.NetworkWatcher;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by admin on 15/06/2018.
 */

public class SmsPresenter implements SmsSentGetTask.OnSmsSentDownloadCompleteListener, SmsReceiveGetTask.OnSmsReceiveDownloadCompleteListener, NetworkWatcher.OnInternetListener {
    private DBActivity act;
    private AppController appController;
    private OnSmsDownloadCallBack listener;
    private View v;
    private boolean hasNext;
    private boolean hasPrev;
    private int selected;

    public SmsPresenter(@NonNull final DBActivity act) {
        this.act = act;
        appController = AppController.getInstance();
    }


    public void startSentDownload() {
        if (act != null) {
            SmsSentGetTask tSent = new SmsSentGetTask(act, Constants.SMS_SENT_URL);
            tSent.setOnSmsSentDownloadCompleteListener(this);
            tSent.execute();
        }
    }

    public void startReceiveDownload() {
        if (act != null) {
            SmsReceiveGetTask tReceive = new SmsReceiveGetTask(act, Constants.SMS_RECEIVE_URL);
            tReceive.setOnSmsReceiveDownloadCompleteListener(this);
            tReceive.execute();
        }
    }


    @Override
    public void onSmsRecDownloadError(boolean hPrev, boolean hNext, @NonNull String prev, @NonNull String next) {
        launchOnNetworkAvai(2);
    }

    @Override
    public void onSmsRecDownloadSucces(@NonNull CopyOnWriteArrayList<Sms> list, boolean hPrev, boolean hNext, @NonNull String prev, @NonNull String next) {

    }

    @Override
    public void onSmsSentDownloadError(boolean hPrev, boolean hNext, @NonNull String prev, @NonNull String next) {
        launchOnNetworkAvai(1);
    }

    @Override
    public void onSmsSentDownloadSucces(@NonNull CopyOnWriteArrayList<Sms> list, boolean hPrev, boolean hNext, @NonNull String prev, @NonNull String next) {

    }

    public void setOnRecipientDownloadCallBack(@NonNull OnSmsDownloadCallBack listener, @NonNull final View view) {
        this.v = view;
        this.listener = listener;
    }

    private void notifyOnRecipientDownloadCallBack(boolean isOk, @NonNull CopyOnWriteArrayList<Sms> list) {
        if (listener != null && v != null) {
            if (isOk)
                listener.onSmsDownloadSucces(list);
            else
                listener.onSmsDownloadFailed();
        }
    }

    private void launchOnNetworkAvai(int sel) {
        if (act != null)
            if (act.watcher != null) {
                selected = sel;
                act.watcher.setOnInternetListener(this);
                act.watcher.isNetworkAvailableWithSilent();
            }
    }

    @Override
    public void onConnected() {
        switch (selected) {
            case 1:
                // Sent Error
                break;
            case 2:
                // Receive Error

                break;
            default:
                break;
        }
        selected = 0;
    }

    @Override
    public void onNotConnected() {

    }

    @Override
    public void onRetry() {

    }

    public static interface OnSmsDownloadCallBack {
        void onSmsDownloadSucces(@NonNull CopyOnWriteArrayList<Sms> list);

        void onSmsDownloadFailed();
    }


}
