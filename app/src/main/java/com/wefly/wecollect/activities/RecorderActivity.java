package com.wefly.wecollect.activities;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.piasy.rxandroidaudio.AudioRecorder;
import com.weflyagri.wecollect.R;

import java.io.File;
import java.util.ArrayList;


public class RecorderActivity extends Activity {
    Button btnSave;
    AudioRecorder mAudioRecorder;
    File mAudioFile;
    String audioPath;
    ArrayList<String> audioPathList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StopRecord();
                finish();
            }
        });

        Thread thread = new Thread() {
            public void run() {
                Log.d("TREAD RUNNNING", "OK");
                initRecord();
            }
        };
        thread.start();
        setResult(200, getIntent());
    }

    private void initRecord() {
        mAudioRecorder = AudioRecorder.getInstance();
        audioPath = Environment
                .getExternalStorageDirectory()
                .getAbsolutePath() +
                File.separator + System.nanoTime() + ".file.m4a";
        mAudioFile = new File(audioPath);
        mAudioRecorder.prepareRecord(MediaRecorder.AudioSource.MIC,
                MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.AudioEncoder.AAC,
                mAudioFile);
        mAudioRecorder.startRecord();
        Log.v("INIT RECORD", "OK");
    }

    @Override
    public void finish() {
        // Create one data intent
        Intent data = new Intent();
        audioPathList.add(audioPath);
        Log.v("RECORD FILEPATH", audioPath);
        data.putExtra("audioPath", audioPath);
        setResult(200, data);
        super.finish();
    }

    public void StopRecord() {
        mAudioRecorder.stopRecord();
    }
}
