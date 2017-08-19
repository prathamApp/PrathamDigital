package com.pratham.prathamdigital.content_playing;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.util.PD_Utility;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.pratham.prathamdigital.content_playing.TextToSp.textToSpeech;


public class Activity_WebView extends AppCompatActivity {

    @BindView(R.id.loadPage)
    WebView webView;
    private Context mContext;
    boolean Resumed = false;

    Context sessionContex;
    VideoPlayer playVideo;
    boolean timer;
    private TextToSp textToSp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionContex = this;
        playVideo = new VideoPlayer();
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);
        String index_path = getIntent().getStringExtra("index_path");
        String path = getIntent().getStringExtra("path");
        String resId = getIntent().getStringExtra("resId");
        textToSp = new TextToSp(this);
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
            webView.addJavascriptInterface(new JSInterface(this, webView, "file://" + parse, textToSp, resId), "Android");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
                    webView.setWebContentsDebuggingEnabled(true);
                }
            }
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
    public void onBackPressed() {
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
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSp != null) {
            textToSp.stopSpeaker();
            Log.d("tts_destroyed", "TTS Destroyed");
        }
    }
}

