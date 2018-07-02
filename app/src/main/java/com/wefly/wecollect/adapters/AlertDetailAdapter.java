package com.wefly.wecollect.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wefly.wecollect.models.Alert;
import com.wefly.wecollect.utils.design.RobotoTextView;
import com.weflyagri.wecollect.R;

import java.util.List;


public class AlertDetailAdapter extends RecyclerView.Adapter<AlertDetailAdapter.AlertViewHolder> {
     Context context;
     private List<Alert> alertList;

    public AlertDetailAdapter(Context ctx, List<Alert> list) {
        this.context = ctx;
        this.alertList = list;
    }

    @Override
    public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_card_item, parent, false);
        return new AlertViewHolder(mView);
    }


    @Override
    public void onBindViewHolder(@NonNull AlertViewHolder holder, int position) {
        Alert alert = alertList.get(position);

        //FILL THE FIELDS
        holder.title.setText(alert.getObject());
        holder.date.setText(alert.getDateCreated());
        holder.content.setText(alert.getContent());
        holder.expediteur.setText("Expeditor");
        holder.label.setText("ALERTE RECU");
        holder.image.setImageResource(R.drawable.email);

    }

    @Override
    public int getItemCount() {
        return alertList.size();
    }


    class AlertViewHolder extends RecyclerView.ViewHolder {

        RobotoTextView label;
        ImageButton btnClose;
        AppCompatImageView image;
        TextView title;
        TextView date;
        EditText content;
        TextView expediteur;

        AlertViewHolder(View convertView) {
            super(convertView);
            //GET THE FIELDS
            label = convertView.findViewById(R.id.label);
            btnClose = convertView.findViewById(R.id.btnClose);
            image = convertView.findViewById(R.id.image);
            title = convertView.findViewById(R.id.title);
            date = convertView.findViewById(R.id.date);
            content = convertView.findViewById(R.id.content);
            expediteur = convertView.findViewById(R.id.expeditor);

        }
    }


}
