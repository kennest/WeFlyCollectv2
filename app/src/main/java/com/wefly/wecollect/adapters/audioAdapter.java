package com.wefly.wecollect.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jean.jcplayer.JcAudio;
import com.example.jean.jcplayer.JcPlayerView;
import com.weflyagri.wecollect.R;

import java.util.ArrayList;

public class audioAdapter extends RecyclerView.Adapter<audioAdapter.Holder> {
    Context context;
    ArrayList<JcAudio> jcAudios = new ArrayList<>();

    public audioAdapter(Context context) {
        this.context = context;
    }

    public void addAudio(String audioPath) {
        jcAudios.add(JcAudio.createFromFilePath(audioPath));
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.audio, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.display(jcAudios);
    }

    @Override
    public int getItemCount() {
        return jcAudios.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private JcPlayerView audioView;


        Holder(View itemView) {
            super(itemView);
            audioView = itemView.findViewById(R.id.jcplayer);
            audioView.initAnonPlaylist(jcAudios);
        }

        void display(ArrayList<JcAudio> jcAudios) {
            audioView.initAnonPlaylist(jcAudios);
        }
    }
}
