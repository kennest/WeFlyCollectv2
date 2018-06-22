package com.wefly.wecollect.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.piasy.rxandroidaudio.AudioRecorder;
import com.weflyagri.wecollect.R;

import java.io.File;


public class RecorderActivity extends Activity {
    Button btnSave;
    AudioRecorder mAudioRecorder;
    File mAudioFile;
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

    private void initRecord(){
        mAudioRecorder = AudioRecorder.getInstance();
        mAudioFile = new File(
                Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + System.nanoTime() + ".file.m4a");
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
        Log.v("RECORD FILEPATH", mAudioFile.getAbsolutePath());
        data.putExtra("filePath", mAudioFile.getPath());
        setResult(200, data);
        super.finish();
    }

    public void StopRecord() {
        mAudioRecorder.stopRecord();
    }
}
