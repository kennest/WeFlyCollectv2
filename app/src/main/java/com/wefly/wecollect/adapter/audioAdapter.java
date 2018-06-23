package com.wefly.wecollect.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jean.jcplayer.JcAudio;
import com.example.jean.jcplayer.JcPlayerView;
import com.weflyagri.wecollect.R;

import java.util.ArrayList;

public class audioAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    ArrayList<JcAudio> jcAudios = new ArrayList<>();

    public audioAdapter(Context context) {
        this.context = context;
    }

    public void addAudio(String audioPath) {
        jcAudios.add(JcAudio.createFromFilePath(audioPath));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.audio, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return jcAudios.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        public JcPlayerView audioView;


        public Holder(View itemView) {
            super(itemView);
            audioView = itemView.findViewById(R.id.jcplayer);
            audioView.initAnonPlaylist(jcAudios);
        }
    }
}
