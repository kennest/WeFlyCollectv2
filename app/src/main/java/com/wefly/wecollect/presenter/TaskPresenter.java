package com.wefly.wecollect.presenter;

import android.os.AsyncTask;

import com.wefly.wecollect.utils.AppController;

/**
 * Created by admin on 01/06/2018.
 */

public class TaskPresenter extends AsyncTask<Void, Integer, Boolean> {

    @Override
    protected void onPreExecute() {
        AppController.addTask(this);
    }


    @Override
    protected Boolean doInBackground(Void... voids) {
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if(isCancelled())
            return;
    }
}
