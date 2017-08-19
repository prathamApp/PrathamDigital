package com.pratham.prathamdigital.content_playing;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.pratham.prathamdigital.R;

public class VideoPlayer extends Activity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    VideoView myVideoView;

    Context sessionContex;
    //   PlayVideo playVideo;
    boolean timer;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionContex = this;
        try {
            setContentView(R.layout.activity_video_player);
            myVideoView = (VideoView) findViewById(R.id.videoView);
            JSInterface.MediaFlag = true;
            String videoPath = getIntent().getStringExtra("path");
            Play(videoPath);
            myVideoView.setOnPreparedListener(this);
            myVideoView.setOnCompletionListener(this);

            if (JSInterface.VideoFlag == 1)
                myVideoView.setMediaController(null);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Play(String path) {
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(myVideoView);
        try {
            myVideoView.setVideoURI(Uri.parse(path));
        } catch (Exception e) {
        }
        myVideoView.setMediaController(mediaController);
        myVideoView.requestFocus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (timer == true) {
            myVideoView.start();
        }

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Runtime rs = Runtime.getRuntime();
        rs.freeMemory();
        rs.gc();
        rs.freeMemory();
        this.finish();
        //JSInterface.MediaFlag=false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        myVideoView.start();
    }

    @Override
    public void onBackPressed() {
        if (JSInterface.VideoFlag == 1) {
            JSInterface.VideoFlag = 0;
            Runtime rs = Runtime.getRuntime();
            rs.freeMemory();
            rs.gc();
            rs.freeMemory();
            this.finish();
            // MainActivity.webView.goBack();
        } else {
            Runtime rs = Runtime.getRuntime();
            rs.freeMemory();
            rs.gc();
            rs.freeMemory();
            this.finish();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
