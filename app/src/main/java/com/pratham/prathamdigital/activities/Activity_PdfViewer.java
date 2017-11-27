package com.pratham.prathamdigital.activities;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.transition.ArcMotion;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.morphing.MorphDialogToFab;
import com.pratham.prathamdigital.custom.morphing.MorphFabToDialog;
import com.pratham.prathamdigital.dbclasses.DatabaseHandler;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.util.PD_Utility;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Activity_PdfViewer extends AppCompatActivity {

    @BindView(R.id.pdf_viewer)
    PDFView pdfView;
    @BindView(R.id.rl_title)
    ViewGroup rl_title;
    @BindView(R.id.pdf_title)
    TextView pdf_title;

    private String myPdf;
    private String StartTime;
    private String resId;
    private boolean backpressedFlag = false;

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        ButterKnife.bind(this);
        myPdf = getIntent().getStringExtra("pdfPath");
        setupEnterTransitions();
        pdf_title.setText(getIntent().getStringExtra("pdfTitle"));
        StartTime = PD_Utility.GetCurrentDateTime();
        resId = getIntent().getStringExtra("resId");
        pdfView.fromUri(Uri.parse(myPdf))
                .enableSwipe(true)
                .swipeHorizontal(true)
                .enableDoubletap(true)
                .defaultPage(0)
                .enableAnnotationRendering(true)
                .password(null)
                .scrollHandle(null)
                .enableAntialiasing(true)
                .load();

        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
    }

    private void setupEnterTransitions() {
        ArcMotion arcMotion = new ArcMotion();
        arcMotion.setMinimumHorizontalAngle(50f);
        arcMotion.setMinimumVerticalAngle(50f);

        Interpolator easeInOut = AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in);

        MorphFabToDialog sharedEnter = new MorphFabToDialog();
        sharedEnter.setPathMotion(arcMotion);
        sharedEnter.setInterpolator(easeInOut);

        MorphDialogToFab sharedReturn = new MorphDialogToFab();
        sharedReturn.setPathMotion(arcMotion);
        sharedReturn.setInterpolator(easeInOut);

        if (rl_title != null) {
            sharedEnter.addTarget(rl_title);
            sharedReturn.addTarget(rl_title);
        }
        getWindow().setSharedElementEnterTransition(sharedEnter);
        getWindow().setSharedElementReturnTransition(sharedReturn);
    }

    @Override
    public void onBackPressed() {
        backpressedFlag = true;
        addScoreToDB();
        setResult(RESULT_CANCELED);
        finishAfterTransition();
    }

    @Override
    protected void onDestroy() {
        if (!backpressedFlag) {
            addScoreToDB();
        }
        Log.d("pdf_activity", "Destroyed");
        super.onDestroy();
    }

    public void addScoreToDB() {
        DatabaseHandler scoreDBHelper = new DatabaseHandler(getApplicationContext());
        Modal_Score modalScore = new Modal_Score();
        modalScore.setSessionId(PrathamApplication.sessionId);
        modalScore.setResourceId(resId);
        modalScore.setScoredMarks(0);
        modalScore.setTotalMarks(0);
        modalScore.setStartTime(StartTime);
        // Unique Device ID
        String deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        modalScore.setDeviceId(deviceId.equals(null) ? "0000" : deviceId);
        modalScore.setEndTime(PD_Utility.GetCurrentDateTime());
        // Location
        String loc = pref.getString("prefLocation", "dummyLocation");
        modalScore.setLocation(loc);
        Log.d("scoreLoc :::",loc);
        // Sent Flag 0 = Local, Sent Flag 1 = pushedToServer
        modalScore.setSentFlag(0);

        scoreDBHelper.addScore(modalScore);
    }

    public static class PdfViewerService extends Service {
        Activity_PdfViewer viewer;

        @Override
        public void onCreate() {
            super.onCreate();
            Log.v("onCreate:", "PDF Service Started");
            viewer = new Activity_PdfViewer();
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onTaskRemoved(Intent rootIntent) {
            Log.v("onTaskRemoved:", "PDF Service task removed");
//            super.onTaskRemoved(rootIntent);
            stopSelf();
        }
    }
}
