package com.wefly.wealert.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
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

import com.fxn.pix.Pix;
import com.github.ybq.android.spinkit.SpinKitView;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.ChipInterface;
import com.wefly.wealert.activities.RecorderActivity;
import com.wefly.wealert.models.Alert;
import com.wefly.wealert.models.Piece;
import com.wefly.wealert.models.Recipient;
import com.wefly.wealert.tasks.AlertPostItemTask;
import com.wefly.wealert.tasks.CategoryGetTask;
import com.wefly.wealert.tasks.PieceUploadTask;
import com.wefly.wealert.utils.AppController;
import com.wefly.wealert.utils.Constants;
import com.weflyagri.wealert.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

public class SendDialogFrament extends DialogFragment {
    AppController appController = AppController.getInstance();
    ImageButton recordBtn = new ImageButton(appController.getApplicationContext());
    Button nextBtn = new Button(appController.getApplicationContext());
    LinearLayout pieceLayout;
    LinearLayout alertform;
    protected List<Recipient> recipientsList;
    static Map<String, Integer> response_category = new HashMap<>();
    private Spinner category;
    ViewGroup vg;
    protected Alert alert = new Alert();
    private EditText edObject, edContent;
    ChooseRecipientDialogFragment recipientDialogFragment = new ChooseRecipientDialogFragment();
    OptionsDialogFragment optionDialog = new OptionsDialogFragment();
    FloatingActionButton menu;
    FragmentManager manager = getFragmentManager();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //We retrieve the recipients from in the bundle
        recipientsList = (List<Recipient>) getArguments().getSerializable("recipients_list");
        Bundle bundle = new Bundle();
        bundle.putSerializable("recipients_list", (Serializable) recipientsList);
        bundle.putSerializable("alert", (Serializable) alert);
        recipientDialogFragment.setArguments(bundle);
        recipientDialogFragment.setCancelable(false);

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        vg = (ViewGroup) inflater.inflate(R.layout.dialog_send, null);
        alertform = vg.findViewById(R.id.alertForm);

        pieceLayout = vg.findViewById(R.id.pieceToSend);
        recordBtn = vg.findViewById(R.id.recordBtn);
        nextBtn = vg.findViewById(R.id.nextBtn);
        edObject = vg.findViewById(R.id.objectEdText);
        edContent = vg.findViewById(R.id.contentEdText);
        menu = vg.findViewById(R.id.menu);

        while(edObject.getText().equals("") && edContent.getText().equals("")){
            nextBtn.setEnabled(false);
            nextBtn.setClickable(false);
        }

        Log.v("piecelist size dialog 1", String.valueOf(appController.getPieceList().size()));

        //Fill Image View
        for (Piece p : appController.getPieceList()) {
            ImageView image = new ImageView(appController.getApplicationContext());
            Log.v("dialog image path", p.getUrl());
            image.setImageURI(p.getContentUrl());
            image.setTag(p.getIndex());
            image.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            image.setScaleType(ImageView.ScaleType.FIT_XY);

            image.setOnClickListener(view -> {
                Toast.makeText(appController.getApplicationContext(), "Piece removed!", Toast.LENGTH_SHORT).show();
                int index = Integer.parseInt(view.getTag().toString());
                Log.v("image clicked index", String.valueOf(index));
                Piece p1 = new Piece();
                p1.setIndex(index);

                List<Piece> pList = appController.getPieceList();
                for (int i = 0; i < pList.size(); i++) {
                    Log.v("image clicked index", String.valueOf(p1.getIndex()));
                    Log.v("image stored index", String.valueOf(pList.get(i).getIndex()));
                    if (pList.get(i).getIndex() == p1.getIndex()) {
                        pList.remove(pList.get(i));
                    }
                }
                appController.setPieceList(pList);

                Log.v("appCtrl plist size", String.valueOf(pList.size()));
                pieceLayout.removeView(view);
            });
            pieceLayout.addView(image);
        }


        //Button clicked listener
        recordBtn.setOnClickListener(v -> {
            Log.v("Audio recordbtn", "clicked");
            Intent recorder = new Intent(getActivity(), RecorderActivity.class);
            startActivityForResult(recorder, 352);
        });

        //Next Button clicked
        nextBtn.setOnClickListener((View v) -> {
            saveInput();
            recipientDialogFragment.show(getFragmentManager(), "RECIPIENT DIALOG");
        });

        //Fab Menu clicked
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Options")
                        .setItems(R.array.options_array, new DialogInterface.OnClickListener() {
                            @SuppressLint("ResourceType")
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                String tag="";
                                switch (which) {
                                    case 0:
                                        tag="terms";
                                        Log.v("Tag selected",tag);
                                        openOptionsDialog(tag);
                                        break;
                                    case 1:
                                        tag="policy";
                                        Log.v("Tag selected",tag);
                                        openOptionsDialog(tag);
                                        break;
                                    case 2:
                                        tag="about";
                                        Log.v("Tag selected",tag);
                                        openOptionsDialog(tag);
                                        break;
                                    default:
                                        tag="about";
                                        Log.v("Tag selected",tag);
                                        openOptionsDialog(tag);
                                        break;
                                }
                            }
                        });
                builder.create().show();
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
        initCategorySpinner();
        return builder.create();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Piece> pList = appController.getPieceList();
        switch (requestCode) {
            case (352): {
                if (resultCode == 200) {
                    if (!data.getExtras().get("audioPath").equals(null)) {
                        Piece audio = new Piece();
                        audio.setIndex(appController.getPieceList().size() + 1);
                        ImageView audioimage = new ImageView(appController.getApplicationContext());
                        audioimage.setImageResource(R.drawable.microphone);
                        audio.setUrl(data.getExtras().getString("audioPath"));

                        pList.add(audio);

                        audioimage.setTag(audio.getIndex());
                        audioimage.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
                        audioimage.setScaleType(ImageView.ScaleType.FIT_XY);
                        recordBtn.setClickable(false);
                        recordBtn.setEnabled(false);

                        audioimage.setOnClickListener(v -> {
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
                            appController.setPieceList(pList);

                            Log.v("appCtrl plist size", String.valueOf(pList.size()));
                            pieceLayout.removeView(v);
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
        appController.setPieceList(new ArrayList<>());
        pieceLayout.removeAllViews();
        Pix.start(getActivity(), Constants.REQUEST_CODE_SELECT_IMAGES, Constants.MAX_SELECT_COUNT);
    }

    //FILL ALERT DATA WITH INPUT
    private void saveInput() throws NullPointerException {
        if (alert != null) {
            alert.setObject(edObject.getText().toString().trim());
            alert.setContent(edContent.getText().toString().trim());
            alert.setCategory(category.getSelectedItem().toString());
        }
    }


    //INIT SPINNER DATA
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
                category_list.add((String) entry.getKey().toString().toUpperCase());
            }
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(appController.getApplicationContext(), android.R.layout.simple_spinner_item, category_list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        category.setAdapter(spinnerAdapter);

    }

    protected void openOptionsDialog(String tag){
        Bundle bundle = new Bundle();
        bundle.putString("tag", tag);
        optionDialog.setArguments(bundle);
        optionDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFragmentTheme);
        optionDialog.show(getFragmentManager(), "Options");
    }
}
