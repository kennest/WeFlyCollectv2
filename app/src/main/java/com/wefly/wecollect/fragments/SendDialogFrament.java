package com.wefly.wecollect.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.fxn.pix.Pix;
import com.pchmn.materialchips.ChipsInput;
import com.wefly.wecollect.activities.RecorderActivity;
import com.wefly.wecollect.models.Alert;
import com.wefly.wecollect.models.Piece;
import com.wefly.wecollect.models.Recipient;
import com.wefly.wecollect.presenters.RecipientPresenter;
import com.wefly.wecollect.tasks.CategoryGetTask;
import com.wefly.wecollect.utils.AppController;
import com.wefly.wecollect.utils.Constants;
import com.weflyagri.wecollect.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

public class SendDialogFrament extends DialogFragment {
    AppController appController = AppController.getInstance();
    ImageButton recordBtn = new ImageButton(appController.getApplicationContext());
    Button sendBtn = new Button(appController.getApplicationContext());
    LinearLayout pieceLayout;
    LinearLayout alertform;
    protected ChipsInput ciRecipients;
    protected CopyOnWriteArrayList<Recipient> recipientsList;
    static Map<String, Integer> response_category = new HashMap<>();
    private Spinner category;
    ViewGroup vg;
    protected Alert alert = new Alert();
    private EditText edObject, edContent;
    protected NumberProgressBar bnp;
    protected CopyOnWriteArrayList<Recipient> recipientsSelected = new CopyOnWriteArrayList<>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        vg = (ViewGroup) inflater.inflate(R.layout.dialog_send, null);
        alertform = vg.findViewById(R.id.alertForm);
        ciRecipients = vg.findViewById(R.id.recipientsCi);

        iniChipInput();
        ShowOrHideForm();
        initCategorySpinner();

        pieceLayout = vg.findViewById(R.id.pieceToSend);
        recordBtn = vg.findViewById(R.id.recordBtn);
        sendBtn = vg.findViewById(R.id.sendBtn);
        edObject = vg.findViewById(R.id.objectEdText);
        edContent = vg.findViewById(R.id.contentEdText);

        Log.v("piecelist size dialog 1", String.valueOf(appController.getPieceList().size()));

        //Fill Image View
        for (Piece p : appController.getPieceList()) {
            ImageView image = new ImageView(appController.getApplicationContext());
            Log.v("dialog image path", p.getUrl());
            image.setImageURI(p.getContentUrl());
            image.setTag(p.getIndex());
            image.setLayoutParams(new LinearLayout.LayoutParams(150, 100));

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(appController.getApplicationContext(), "Piece removed!", Toast.LENGTH_SHORT).show();
                    int index = Integer.parseInt(v.getTag().toString());
                    Log.v("image clicked index", String.valueOf(index));
                    Piece p = new Piece();
                    p.setIndex(index);

                    List<Piece> pList = appController.getPieceList();

                    for (int i = 0; i < pList.size(); i++) {
                        Log.v("image clicked index", String.valueOf(p.getIndex()));
                        Log.v("image stored index", String.valueOf(pList.get(i).getIndex()));
                        if (pList.get(i).getIndex() == p.getIndex()) {
                            pList.remove(pList.get(i));
                            ShowOrHideForm();
                        }
                    }

                    Log.v("piecelist size dialog 2", String.valueOf(pList.size()));
                    pieceLayout.removeView(v);
                }
            });
            pieceLayout.addView(image);
        }

        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("Audio recordbtn", "clicked");
                Intent recorder = new Intent(getActivity(), RecorderActivity.class);
                startActivityForResult(recorder, 352);
            }
        });
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(vg);
        // Add action buttons
//                .setPositiveButton(R.string.signin, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                        // sign in the user ...
//                    }
//                })
//                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        LoginDialogFragment.this.getDialog().cancel();
//                    }
//                });
        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Piece> pList = appController.getPieceList();
        iniChipInput();
        switch (requestCode) {
            case (352): {
                if (resultCode == 200) {
                    if (!data.getExtras().get("audioPath").equals(null)) {
                        Piece audio = new Piece();
                        audio.setIndex(appController.getPieceList().size() + 1);
                        ImageView audioimage = new ImageView(appController.getApplicationContext());
                        audioimage.setImageResource(R.drawable.microphone);
                        audio.setUrl(data.getExtras().getString("audioPath"));
                        audioimage.setTag(audio.getIndex());
                        audioimage.setLayoutParams(new LinearLayout.LayoutParams(150, 100));
                        recordBtn.setClickable(false);
                        recordBtn.setEnabled(false);

                        audioimage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(appController.getApplicationContext(), "Piece removed!", Toast.LENGTH_SHORT).show();
                                int index = Integer.parseInt(v.getTag().toString());
                                Log.v("image clicked index", String.valueOf(index));
                                Piece p = new Piece();
                                p.setIndex(index);

                                for (int i = 0; i < pList.size(); i++) {
                                    Log.v("image clicked index", String.valueOf(p.getIndex()));
                                    Log.v("image stored index", String.valueOf(pList.get(i).getIndex()));
                                    if (pList.get(i).getIndex() == p.getIndex()) {
                                        pList.remove(pList.get(i));
                                    }
                                    recordBtn.setClickable(true);
                                    recordBtn.setEnabled(true);
                                }
                                ShowOrHideForm();

                                Log.v("piecelist size dialog 3", String.valueOf(pList.size()));
                                pieceLayout.removeView(v);
                            }
                        });

                        pieceLayout.addView(audioimage);
                    }
                }
            }
            break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Pix.start(getActivity(), Constants.REQUEST_CODE_SELECT_IMAGES, Constants.MAX_SELECT_COUNT);
    }

    public void ShowOrHideForm() {
        if (appController.getPieceList().size() <= 0) {
            alertform.setVisibility(View.INVISIBLE);
            sendBtn.setClickable(false);
            sendBtn.setEnabled(false);
        } else {
            alertform.setVisibility(View.VISIBLE);
            sendBtn.setClickable(true);
            sendBtn.setEnabled(true);
        }
    }

    protected void iniChipInput() {
        // get Recipients
        Log.d("INIT CHIP INPUT", "OK");
        RecipientPresenter p = new RecipientPresenter(getActivity());
        recipientsList = p.getRecipients(); // Auto download if empty
        // FIX: UnsupportedOperationException
        try {
            ArrayList<Recipient> rList = new ArrayList<>();
            rList.addAll(recipientsList);
            Log.d("INIT CHIP INPUT", "LIST addAll Run");

            if (rList.size() > 0) {
                if (ciRecipients != null) {
                    Log.d("INIT CHIP INPUT", "LIST ciRecipients NOT NULL Run");
                    ciRecipients.setFilterableList(rList);
                } else {
                    Log.d("INIT CHIP INPUT", "LIST ciRecipients IS NULL Run");
                }


            } else {
                Log.d("INIT CHIP INPUT", "LIST ciRecipients IS EMPTY Run");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("INIT CHIP INPUT", "CHIP UnsupportedOperationException");
        }
    }

    private void saveInput() throws NullPointerException {
        if (alert != null) {
            alert.setObject(edObject.getText().toString().trim());
            alert.setContent(edContent.getText().toString().trim());
            alert.setCategory(category.getSelectedItem().toString());
            recipientsSelected.clear();
            List<Recipient> list = (List<Recipient>) ciRecipients.getSelectedChipList();
            recipientsSelected.addAll(list);
            if (recipientsSelected.size() > 0)
                alert.setRecipients(recipientsSelected);
        }
    }

    protected void initCategorySpinner() {
        //Execute category task in order to get the list
        CategoryGetTask task = new CategoryGetTask(appController);
        try {
            response_category = task.execute().get();
            appController.setAlert_categories(response_category);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        category = vg.findViewById(R.id.categorySpinner);

        //Load Category list in spinner
        List<String> category_list = new ArrayList<>();
        if (response_category != null) {
            for (Map.Entry entry : response_category.entrySet()) {
                category_list.add((String) entry.getKey());
            }
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(appController.getApplicationContext(), android.R.layout.simple_spinner_item, category_list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(spinnerAdapter);

    }

}
