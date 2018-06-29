package com.wefly.wecollect.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.wefly.wecollect.models.Alert;
import com.wefly.wecollect.utils.design.RobotoTextView;
import com.weflyagri.wecollect.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AlertAdapter extends BaseAdapter {
    private List<Alert> alertList;
    private Context context;
    private LayoutInflater inflater;

    public AlertAdapter(Context ctx,List<Alert> alertList) {
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
        v = inflater.inflate(R.layout.row_item,null);
        RobotoTextView objet=v.findViewById(R.id.labelTView);
        RobotoTextView expediteur=v.findViewById(R.id.expeditorTView);
        CircleImageView image=v.findViewById(R.id.pImage);
        RobotoTextView date=v.findViewById(R.id.dateTView);

        Alert alert=getItem(position);
        //Fill the fields
        objet.setText(alert.getObject());
        expediteur.setText(alert.getRecipientsString());
        date.setText(alert.getDateCreated());
        image.setImageResource(R.drawable.email);

        v.setOnClickListener(view-> Toast.makeText(context,"Alert Clicked!",Toast.LENGTH_LONG).show());

        return v;
    }
}
