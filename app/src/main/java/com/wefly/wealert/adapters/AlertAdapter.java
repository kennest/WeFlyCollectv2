package com.wefly.wealert.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.wefly.wealert.models.Alert;
import com.wefly.wealert.utils.AppController;
import com.wefly.wealert.utils.design.RobotoTextView;
import com.weflyagri.wealert.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AlertAdapter extends BaseAdapter {
    private List<Alert> alertList;
    private Context context;
    private LayoutInflater inflater;
    AppController appController=AppController.getInstance();

    public AlertAdapter(Context ctx, List<Alert> alertList) {
        this.alertList = alertList;
        this.context = ctx;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return alertList.size();
    }

    @Override
    public Alert getItem(int position) {
        return alertList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View v, ViewGroup parent) {
        v = inflater.inflate(R.layout.row_item, null);
        RobotoTextView objet = v.findViewById(R.id.labelTView);
        RobotoTextView expediteur = v.findViewById(R.id.expeditorTView);
        CircleImageView image = v.findViewById(R.id.pImage);
        RobotoTextView date = v.findViewById(R.id.dateTView);

        Alert alert = getItem(position);
        //Fill the fields
        objet.setText(alert.getObject());
        expediteur.setText(alert.getRecipientsString());
        date.setText(alert.getDateCreated());
        image.setImageResource(R.drawable.email);

        v.setOnClickListener(view ->
                Toast.makeText(context, "Alert Clicked!", Toast.LENGTH_LONG).show()

        );
        return v;
    }

    public List<Alert> extractListFromPrefs() {
        Alert alert=new Alert();
        List<Alert> list=new ArrayList<>();

        SharedPreferences sp = appController.getSharedPreferences("alert_receive_data", 0);
        SharedPreferences.Editor editor = sp.edit();
        try {
            JSONObject resultJSON=new JSONObject(sp.getString("alert_responses","VIDE"));
            JSONArray results=resultJSON.getJSONArray("results");
            for(int i=0;i<=results.length();i++){
                JSONObject a=results.getJSONObject(i);
                alert.setAlertId((Integer)(a.get("id")));
                alert.setContent(a.getString("contenu"));
                alert.setDateCreated(a.getString("date_de_creation"));
                alert.setExpediteur(a.getJSONObject("user").getString("username"));
                alert.setCategory(a.getString("categorie"));

                list.add(alert);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editor.apply();
        return list;
    }
}
