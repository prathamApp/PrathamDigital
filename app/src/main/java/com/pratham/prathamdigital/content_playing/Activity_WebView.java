package com.pratham.prathamdigital.content_playing;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.dbclasses.DatabaseHandler;
import com.pratham.prathamdigital.interfaces.Interface_Score;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.util.PD_Utility;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Activity_WebView extends AppCompatActivity implements Interface_Score {

    @BindView(R.id.loadPage)
    WebView webView;
    private Context mContext;
    boolean Resumed = false;

    Context sessionContex;
    VideoPlayer playVideo;
    boolean timer;
    private String startTime;
    private boolean backpressedFlag = false;
    public static int totalMarks = 0;
    public static int scoredMarks = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionContex = this;
        playVideo = new VideoPlayer();
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);
        startTime = PD_Utility.GetCurrentDateTime();

        String index_path = getIntent().getStringExtra("index_path");
        String path = getIntent().getStringExtra("path");
        String resId = getIntent().getStringExtra("resId");
        createWebView(index_path, path, resId);
    }

    public void createWebView(String GamePath, String parse, String resId) {

        try {
            webView.loadUrl("file:///" + GamePath);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
            }
            webView.addJavascriptInterface(new JSInterface(Activity_WebView.this, webView,
                    "file://" + parse, resId, Activity_WebView.this), "Android");

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
            webView.setWebContentsDebuggingEnabled(true);
//                }
//            }
            webView.setWebViewClient(new WebViewClient());
            webView.setWebChromeClient(new WebChromeClient());
            webView.clearCache(true);

            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        if (!backpressedFlag)
            addScoreToDB();
        Log.d("Destroyed Score Entry", "Destroyed Score Entry");
//        if (tts != null) {
//            tts.shutdown();
//            Log.d("tts_destroyed", "TTS Destroyed");
//        }
        super.onDestroy();
    }

    public void addScoreToDB() {

        DatabaseHandler scoreDBHelper = new DatabaseHandler(getApplicationContext());
        Modal_Score modalScore = new Modal_Score();
        modalScore.setSessionId(PrathamApplication.sessionId);
        modalScore.setResourceId(getIntent().getStringExtra("resId"));
        modalScore.setScoredMarks(scoredMarks);
        modalScore.setTotalMarks(totalMarks);
        modalScore.setStartTime(startTime);
        String deviceId = Build.SERIAL;
        modalScore.setDeviceId(deviceId);
        modalScore.setEndTime(PD_Utility.GetCurrentDateTime());
        scoreDBHelper.addScore(modalScore);
    }

    @Override
    public void onBackPressed() {
        addScoreToDB();
        backpressedFlag = true;
        webView.post(new Runnable() {
            public void run() {
                //String jsString = "javascript:Utils.closeAllAudios()";
                //webView.loadUrl(jsString);
                //JSInterface.stopTtsBackground();
                webView.loadUrl("about:blank");
            }
        });
        super.onBackPressed();
        webView.clearCache(true);
        Runtime rs = Runtime.getRuntime();
        rs.freeMemory();
        rs.gc();
        rs.freeMemory();
        finish();
    }

    @Override
    public void setScore(int scoredMarks, int totalMarks) {
        this.scoredMarks += scoredMarks;
        this.totalMarks += totalMarks;
    }

    public static class WebViewService extends Service {
        Activity_WebView activity_webView;

        @Override
        public void onCreate() {
            super.onCreate();
            Log.v("Ketan:", "WebView Service Started");
            activity_webView = new Activity_WebView();
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onTaskRemoved(Intent rootIntent) {
            Log.v("Ketan:", "WebView Service task removed");
//            super.onTaskRemoved(rootIntent);
            stopSelf();
        }
    }
}

