package com.wefly.wecollect.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.wefly.wecollect.adapters.RecipientAdapter;
import com.wefly.wecollect.models.Alert;
import com.wefly.wecollect.models.Recipient;
import com.wefly.wecollect.tasks.AlertPostItemTask;
import com.wefly.wecollect.tasks.PieceUploadTask;
import com.wefly.wecollect.utils.AppController;
import com.weflyagri.wecollect.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChooseRecipientDialogFragment extends DialogFragment {
    private List<Recipient> recipientList = new ArrayList<>();
    private Alert alert = new Alert();
    AppController appController=AppController.getInstance();
    Set<String> recipient_ids=new HashSet<>();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog.Builder loaderbuilder = new AlertDialog.Builder(getActivity());

        //We retrieve the recipientlist and alert from bundle
        recipientList = (List<Recipient>) getArguments().getSerializable("recipients_list");
        alert = (Alert) getArguments().getSerializable("alert");

        appController.setRecipients(recipientList);

        // Set the adapter
        builder.setTitle("CHOOSE RECIPIENTS:");
        builder.setCancelable(false);
        builder.setAdapter(
                new RecipientAdapter(getContext(), recipientList), (dialog, which) -> {
                    Toast.makeText(getContext(), "Item Clicked Again!", Toast.LENGTH_LONG).show();
                })
                // Set the action buttons
                .setPositiveButton("SEND NOW",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                AlertPostItemTask task = new AlertPostItemTask(alert);
                                task.execute();

                                loaderbuilder.setMessage("Processing...");
                                loaderbuilder.setView(R.layout.alert_send_loading);

                                // Create the AlertDialog object and return it
                                AlertDialog alertLoaderDialog = loaderbuilder.create();
                                alertLoaderDialog.setCancelable(false);
                                alertLoaderDialog.setCanceledOnTouchOutside(false);

                                alertLoaderDialog.show();

                                task.setOnAlertSendListener(new AlertPostItemTask.OnAlertSendListener() {
                                    @Override
                                    public void onSendError(@NonNull Alert e) {

                                    }

                                    @Override
                                    public void onSendSucces(@NonNull Alert e) {
                                    }
                                });

                                task.setOnPieceSendListener(new PieceUploadTask.OnPieceSendListener() {
                                    @Override
                                    public void onUploadError() {

                                    }

                                    @Override
                                    public void onUploadSucces() {
                                        try {
                                            Thread.sleep(2500);
                                            alertLoaderDialog.cancel();
                                            loaderbuilder.setMessage("Task Completed!")
                                                    .setView(R.layout.alert_sent_loading)
                                                    .setNegativeButton("Return", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.dismiss();
                                                            dismiss();
                                                        }
                                                    })
                                                    .setPositiveButton("Show sent alerts list...", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {

                                                        }
                                                    });
                                            // Create the AlertDialog object and return it
                                            AlertDialog alertLoaderDialog2 = loaderbuilder.create();
                                            alertLoaderDialog2.setCancelable(false);
                                            alertLoaderDialog2.setCanceledOnTouchOutside(false);
                                            alertLoaderDialog2.show();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("RETURN", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });

        AlertDialog alertDialog = builder.create();

        ListView listView = alertDialog.getListView();
        listView.setAdapter(new RecipientAdapter(getContext(), recipientList));
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox selected = (CheckBox) view;
            }
        });
        listView.setDivider(null);
        listView.setDividerHeight(-1);
        listView.setBackgroundColor(Color.parseColor("#599E47"));

        //We prepared a String set to store the id of the recipients
        SharedPreferences sp=getContext().getSharedPreferences("recipients",0);
        SharedPreferences.Editor editor=sp.edit();
        editor.putStringSet("recipient_id",new HashSet<String>());

        return alertDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
