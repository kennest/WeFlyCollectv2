package com.wefly.wecollect.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.wefly.wecollect.models.Email;
import com.wefly.wecollect.presenters.DataBasePresenter;
import com.wefly.wecollect.utils.Constants;


/**
 * Created by admin on 02/04/2018.
 */

public class EmailCreateTask extends AsyncTask<Void, Integer, Boolean> {
    public View v;
    private Email email;
    private Activity act;
    private String TAG = getClass().getSimpleName();
    private OnEmailSaveCompleteListener listener;


    public EmailCreateTask(@NonNull final Activity activity, @NonNull Email alert) {
        this.email = alert;
        this.act = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            DataBasePresenter.getInstance().addEmail(email);
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
        notifyOnSavingDoneListener(isOk, email);
    }

    public void setOnEmailSaveCompleteListener(@NonNull OnEmailSaveCompleteListener listener, @NonNull View view) {
        this.listener = listener;
        this.v = view;

    }

    private void notifyOnSavingDoneListener(boolean isDone, @NonNull Email alert) {
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

    public interface OnEmailSaveCompleteListener {
        void onSaveError(@NonNull Email e, @NonNull View view);

        void onSaveSucces(@NonNull Email e, @NonNull View view);
    }
}
