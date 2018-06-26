package com.wefly.wecollect.presenters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.wefly.wecollect.models.Alert;
import com.wefly.wecollect.models.Email;
import com.wefly.wecollect.models.Recipient;
import com.wefly.wecollect.models.Sms;
import com.wefly.wecollect.tasks.AlertCreateTask;
import com.wefly.wecollect.tasks.AlertPostItemTask;
import com.wefly.wecollect.tasks.EmailCreateTask;
import com.wefly.wecollect.tasks.EmailPostItemTask;
import com.wefly.wecollect.tasks.PieceUploadTask;
import com.wefly.wecollect.tasks.SmsCreateTask;
import com.wefly.wecollect.tasks.SmsPostItemTask;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by admin on 27/03/2018.
 */

public class DBActivity extends BaseActivity implements SmsPostItemTask.OnSmsSendListener, EmailPostItemTask.OnEmailSendListener, AlertPostItemTask.OnAlertSendListener, SmsCreateTask.OnSmsSaveCompleteListener, EmailCreateTask.OnEmailSaveCompleteListener, AlertCreateTask.OnAlertSaveCompleteListener {

    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBasePresenter.init(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            DataBasePresenter.getInstance().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    protected void onDiplayParcelleDetails(@NonNull Activity act, @NonNull final Parcelle p){
//
//        Intent formIntent = new Intent(act, ParcellesActivity.class);
//        formIntent.putExtra("showDetail", true);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("parcelObj", p);
//        formIntent.putExtras(bundle);
//        startActivity(formIntent);
//        AppController.clearDestroyList();
//    }

    //Recupere la liste des destinataires dans la DB Sqlite
    protected @NonNull
    CopyOnWriteArrayList<Recipient> getRecipients(final Context ctx) {
        CopyOnWriteArrayList<Recipient> list = new CopyOnWriteArrayList<>();
        try {
            list.addAll(DataBasePresenter.getInstance().getRecipients(ctx));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    //Envoi un sms
    protected void sendSms(@NonNull Sms sSms, @NonNull final DBActivity activity) {
        SmsPostItemTask task = new SmsPostItemTask(sSms, activity);
        task.setOnSmsSendListener(this);
        task.execute();
    }

    //Envoi un email
    protected void sendEmail(@NonNull Email email) {
        EmailPostItemTask task = new EmailPostItemTask(email);
        task.setOnEmailSendListener(this);
        task.execute();
    }

    protected void uploadPiece(@Nullable Email email, @Nullable Alert alert) {
        Log.v("DBActivity upload piece", "RUN");
        PieceUploadTask task = new PieceUploadTask(appController.getPieceList(), email, alert);
        task.execute();
    }

    //Envoi une alert
    protected void sendAlert(@NonNull Alert alert) {
        Log.v(TAG + "send alert", "RUN");
        AlertPostItemTask task = new AlertPostItemTask(alert);
        task.execute();
    }

    //Sauvegarde un sms dans la DB Sqlite
    protected void addSms(@Nullable final Activity act, @Nullable Sms sms) {
        SmsCreateTask task = new SmsCreateTask(act, sms);
        task.setOnSmsSaveCompleteListener(this);
        task.execute();
    }

    @Override
    public void onSendError(@NonNull Sms s) {

    }

    @Override
    public void onSendErrorNetwork(@NonNull Sms s) {

    }

    @Override
    public void onSendSucces(@NonNull Sms s) {

    }

    @Override
    public void onSendError(@NonNull Email e) {

    }

    @Override
    public void onSendSucces(@NonNull Email e) {

    }

    @Override
    public void onSaveError(@NonNull Email e, @NonNull View view) {

    }

    @Override
    public void onSaveSucces(@NonNull Email e, @NonNull View view) {

    }

    @Override
    public void onSaveError(@NonNull Alert a, @NonNull View view) {

    }

    @Override
    public void onSaveSucces(@NonNull Alert a, @NonNull View view) {

    }

    @Override
    public void onSaveError(@NonNull Sms s) {

    }

    @Override
    public void onSaveSucces(@NonNull Sms s) {

    }

    @Override
    public void onSendError(@NonNull Alert e) {

    }

    @Override
    public void onSendSucces(@NonNull Alert e) {

    }
}
