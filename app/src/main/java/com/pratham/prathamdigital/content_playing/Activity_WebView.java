package com.pratham.prathamdigital.content_playing;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pratham.prathamdigital.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Activity_WebView extends AppCompatActivity {

    @BindView(R.id.loadPage)
    WebView webView;
    private Context mContext;
    boolean Resumed = false;

    Context sessionContex;
    VideoPlayer playVideo;
    boolean timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionContex = this;
        playVideo = new VideoPlayer();
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);
        String gamePath = getIntent().getStringExtra("path");
//        webResId=getIntent().getStringExtra("resId");
        createWebView(Uri.parse(gamePath));


    }

    public void createWebView(Uri GamePath) {

        String myPath = GamePath.toString();

        webView = (android.webkit.WebView) findViewById(R.id.loadPage);
        webView.loadUrl(myPath);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.addJavascriptInterface(new JSInterface(this, webView, myPath), "Android");

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

}

