package com.wefly.wecollect.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.wefly.wecollect.models.Sms;
import com.wefly.wecollect.presenters.DataBasePresenter;
import com.weflyagri.wecollect.R;

import pl.tajchert.waitingdots.DotsTextView;


/**
 * Created by admin on 02/04/2018.
 */

public class SmsCreateTask extends AsyncTask<Void, Integer, Boolean> {
    private Sms sms;
    private Activity act;
    private String TAG = getClass().getSimpleName();
    private OnSmsSaveCompleteListener listener;
    private DotsTextView dotsTView;


    public SmsCreateTask(@NonNull final Activity activity, @NonNull Sms sms) {
        this.sms = sms;
        this.act = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            dotsTView = act.findViewById(R.id.dots);
            dotsTView.showAndPlay();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            DataBasePresenter.getInstance().addSms(sms);
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

        try {
            dotsTView.hideAndStop();
            notifyOnSavingDoneListener(isOk, sms);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setOnSmsSaveCompleteListener(@NonNull OnSmsSaveCompleteListener listener) {
        this.listener = listener;

    }

    private void notifyOnSavingDoneListener(boolean isDone, @NonNull Sms sms) {
        if (listener != null) {
            try {
                if (isDone)
                    listener.onSaveSucces(sms);
                else
                    listener.onSaveError(sms);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface OnSmsSaveCompleteListener {
        void onSaveError(@NonNull Sms s);

        void onSaveSucces(@NonNull Sms s);
    }
}
