package com.wefly.wecollect.presenter;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.wefly.wecollect.model.Recipient;
import com.wefly.wecollect.task.RecipientsGetTask;
import com.wefly.wecollect.utils.AppController;
import com.wefly.wecollect.utils.Constants;
import com.wefly.wecollect.utils.NetworkWatcher;
import com.wefly.wecollect.utils.Utils;
import com.weflyagri.wecollect.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by admin on 15/06/2018.
 */

public class RecipientPresenter implements RecipientsGetTask.OnRecipientsDownloadCompleteListener, NetworkWatcher.OnInternetListener{
    private FormActivity act;
    private String stRecipients = "";
    private String nextPage = "", prevPage = "";
    private boolean hasNext;
    private boolean hasPrevious;
    RecipientsGetTask task;
    private NetworkWatcher watcher;
    private int selected;
    private boolean withNext;
    private AppController appController;
    private CopyOnWriteArrayList<Recipient> list = new CopyOnWriteArrayList<>();
    private OnRecipientDownloadCallBack listener;
    private String TAG = getClass().getSimpleName();
    private int mMax;
    final NumberProgressBar pCounter;

    public RecipientPresenter(@NonNull final  FormActivity act){
        this.act = act;
        this.appController = AppController.getInstance();
        this.pCounter =  act.getCounter();
    }

    public void downloadAllRecipients(boolean goToNext){
        if (appController != null){
            if (appController.isTokenValide()){
                if (goToNext)
                    withNext =  true;
                else
                    withNext = false;
                launchOnNetworkAvai(2);
            }
        }

    }

    public CopyOnWriteArrayList<Recipient> getRecipients(){
        CopyOnWriteArrayList<Recipient> list = new CopyOnWriteArrayList<>();
        if (act != null){
            list.addAll(act.getRecipients(act));
            if (list.size() == 0){
                //454545
                onDownload();
            }

        }
        return list;
    }

    private void onDownload() {
        if (withNext)
            downloadAllRecipients(true);
        else
            downloadAllRecipients(false);
    }

    public static interface OnRecipientDownloadCallBack {
        void onRecipientDownloadSucces();
    }

    public void setOnRecipientDownloadCallBack(@NonNull OnRecipientDownloadCallBack listener) {
        this.listener = listener;
    }

    private void notifyOnRecipientDownloadCallBack() {
        if (listener != null){
            Log.v(Constants.APP_NAME, TAG + " notifyOnRecipientDownloadCallBack RUN");
            listener.onRecipientDownloadSucces();
        }
    }


    @Override
    public void onDownloadError(@NonNull String errorMsg) {
        // Retry
        launchOnNetworkAvai(1);
    }

    @Override
    public void onDownloadSucces(@NonNull CopyOnWriteArrayList<Recipient> recipentsArray, boolean hPrev, boolean hNext, @NonNull String prev, @NonNull String next, int max) {
        try {
            this.hasNext        = hNext;
            this.hasPrevious    = hPrev;
            this.nextPage       = next;
            this.prevPage       = prev;
            this.mMax           = max;
            this.list.addAll(recipentsArray);

            // download not finish
            if (hasNext) {
                Log.v(Constants.APP_NAME, TAG + " download NOT finish RUN hasNext " +  hasNext +" next = " + next + " hasprev " + hasPrevious + " prev = " + prev);
                downloadAllRecipients(true);
            }
            else{
                // download finish
                //Clearn old
                DataBasePresenter.getInstance().clearRecipients(act);
                //Save New
                if (appController != null){
                    DataBasePresenter.getInstance().saveRecipientList(act, appController.recipientListToJSONArr((list)));
                }
                notifyOnRecipientDownloadCallBack();
                Log.v(Constants.APP_NAME, TAG + " download COMPLETE RUN" +  hasNext +" next = " + next + " hasprev " + hasPrevious + " prev = " + prev);
            }

            // Nofity
            if (act != null){
                act.setCounterMax(mMax);
                act.notifCounter(list.size());
            }

            Log.v(Constants.APP_NAME, TAG + " onDownloadSucces END");

        }catch (Exception e){
            e.printStackTrace();
            Log.v(Constants.APP_NAME, TAG + " onDownloadSucces SAVE FAILD");
        }
    }

    private void launchOnNetworkAvai(int sel) {
        if (act != null)
            if (act.watcher != null){
                selected = sel;
                act.watcher.setOnInternetListener(this);
                act.watcher.isNetworkAvailableWithSilent();
            }
    }


    @Override
    public void onConnected() {
        switch (selected){
            case 1:
                // Retry
                onDownload();
                break;
            case 2:
                // new Download
                if (withNext)
                    task = new RecipientsGetTask(act, nextPage);
                else
                    task = new RecipientsGetTask(act, Constants.RECIPIENTS_URL);
                task.setOnRecipientsDownloadCompleteListener(this);
                task.execute();
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
}