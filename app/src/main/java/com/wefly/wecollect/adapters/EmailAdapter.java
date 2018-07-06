package com.wefly.wecollect.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.wefly.wecollect.models.Email;
import com.wefly.wecollect.utils.design.RobotoTextView;
import com.weflyagri.wecollect.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmailAdapter extends BaseAdapter {
    private List<Email> list;
    private Context context;
    private LayoutInflater inflater;

    public EmailAdapter(Context ctx, List<Email> list) {
        this.list = list;
        this.context=ctx;
        this.inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Email getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView=inflater.inflate(R.layout.row_item,null);

        RobotoTextView objet=convertView.findViewById(R.id.labelTView);
        RobotoTextView expediteur=convertView.findViewById(R.id.expeditorTView);
        CircleImageView image=convertView.findViewById(R.id.pImage);
        RobotoTextView date=convertView.findViewById(R.id.dateTView);

        Email e=getItem(position);

        objet.setText(e.getObject());
        expediteur.setText(e.getExpediteur());
        date.setText(e.getDateCreated());
        image.setImageResource(R.drawable.email);

        convertView.setOnClickListener(v -> {
            Toast.makeText(context, expediteur.getText() + " clicked!", Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }
}
