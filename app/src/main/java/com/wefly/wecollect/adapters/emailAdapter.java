package com.wefly.wecollect.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wefly.wecollect.models.Email;
import com.wefly.wecollect.utils.design.RobotoTextView;
import com.weflyagri.wecollect.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class emailAdapter extends RecyclerView.Adapter<emailAdapter.Holder> {

    List<Email> list;

    public emailAdapter(List<Email> list) {
        this.list = list;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.row_item, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.display(list.get(position));
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    class Holder extends RecyclerView.ViewHolder {

        CircleImageView image;
        RobotoTextView date;
        RobotoTextView objet;
        //RobotoTextView expediteur;

        CardView emailView;

        public Holder(View itemView) {
            super(itemView);

            emailView = itemView.findViewById(R.id.card_view_layout);

            image = itemView.findViewById(R.id.pImage);
            date = itemView.findViewById(R.id.dateTView);
            objet = itemView.findViewById(R.id.labelTView);
            //expediteur = itemView.findViewById(R.id.expeditorTView);

        }

        void display(Email email) {
            image.setImageResource(R.drawable.email);
            date.setText(email.getDateCreated());
            objet.setText(email.getObject());
            //expediteur.setText((CharSequence) email.getRecipientsIds());
        }
    }
}
