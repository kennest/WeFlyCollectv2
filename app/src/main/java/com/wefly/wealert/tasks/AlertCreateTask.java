package com.wefly.wealert.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.wefly.wealert.models.Alert;
import com.wefly.wealert.presenters.DataBasePresenter;
import com.wefly.wealert.utils.Constants;


/**
 * Created by admin on 02/04/2018.
 */

public class AlertCreateTask extends AsyncTask<Void, Integer, Boolean> {
    public View v;
    private Alert alert;
    private Activity act;
    private String TAG = getClass().getSimpleName();
    private OnAlertSaveCompleteListener listener;


    public AlertCreateTask(@NonNull final Activity activity, @NonNull Alert alert) {
        this.alert = alert;
        this.act = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            DataBasePresenter.getInstance().addAlert(alert);
            DataBasePresenter.getInstance().close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    @Override
    protected void onPostExecute(Boolean isOk) {
        super.onPostExecute(isOk);
        if (isOk) {
            Log.v(Constants.APP_NAME, TAG + " Save Done");
        } else {
            Log.v(Constants.APP_NAME, TAG + " Save Error");
        }
        notifyOnSavingDoneListener(isOk, alert);
    }

    public void setOnParcelleSaveListener(@NonNull OnAlertSaveCompleteListener listener, @NonNull View view) {
        this.listener = listener;
        this.v = view;
    }

    private void notifyOnSavingDoneListener(boolean isDone, @NonNull Alert alert) {
        if (listener != null && v != null) {
            try {
                if (isDone)
                    listener.onSaveSucces(alert, v);
                else
                    listener.onSaveError(alert, v);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface OnAlertSaveCompleteListener {
        void onSaveError(@NonNull Alert a, @NonNull View view);

        void onSaveSucces(@NonNull Alert a, @NonNull View view);
    }
}
