package com.wefly.wecollect.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wefly.wecollect.models.Recipient;
import com.wefly.wecollect.utils.AppController;
import com.weflyagri.wecollect.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class RecipientAdapter extends BaseAdapter {
    private Context context;
    private List<Recipient> recipientSet = new ArrayList<>();
    Set<String> recipient_ids = new HashSet<String>();
    private LayoutInflater inflater;
    AppController appController = AppController.getInstance();

    public RecipientAdapter(Context ctx, List<Recipient> recipientSet) {
        this.context = ctx;
        this.recipientSet = recipientSet;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return recipientSet.size();
    }

    @Override
    public Recipient getItem(int position) {
        return recipientSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.recipient_item, parent, false);

        TextView id = convertView.findViewById(R.id.id);
        ImageView avatar = convertView.findViewById(R.id.avatar);
        TextView username = convertView.findViewById(R.id.username);
        TextView email = convertView.findViewById(R.id.email);

        Recipient r = getItem(position);

        id.setText(String.valueOf(r.getRecipientId()));
        avatar.setImageResource(R.drawable.avatar);
        username.setText(r.getUserName());
        email.setText(r.getEmail());

        username.setTextColor(Color.WHITE);
        username.setTypeface(Typeface.DEFAULT_BOLD);
        username.setTextSize(18);
        email.setTextColor(Color.WHITE);
        convertView.setTag(r);

        convertView.setOnClickListener(v -> {
            //On recupere une Set de String vide du sharedPrefs
            SharedPreferences sp = context.getSharedPreferences("recipients", 0);

            //On ajoute l'ID du recipient selectionnee
            recipient_ids = sp.getStringSet("recipients_id", new HashSet<String>());

            Snackbar.make(v, "LIST SIZE 0" +recipient_ids.size()+ " recipient(s)!", Snackbar.LENGTH_SHORT).show();

            String recipient_id = String.valueOf(((Recipient) v.getTag()).getRecipientId());
            if (recipient_ids.add(recipient_id)) {
                v.setBackgroundColor(Color.parseColor("#134200"));
            } else {
                recipient_ids.remove(recipient_id);
                v.setBackgroundColor(Color.parseColor("#599E47"));
            }
            sp.edit().putStringSet("recipients_id", recipient_ids).commit();

            //DEBUG
            recipient_ids = sp.getStringSet("recipients_id", new HashSet<String>());
            Snackbar.make(v, "LIST SIZE 0" +recipient_ids.size()+ " recipient(s)!", Snackbar.LENGTH_SHORT).show();
        });
        return convertView;
    }
}
