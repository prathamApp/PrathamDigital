package com.pratham.prathamdigital.activities;

import android.animation.Animator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.content_playing.TextToSp;
import com.pratham.prathamdigital.custom.custom_fab.FloatingActionButton;
import com.pratham.prathamdigital.custom.reveal.ViewAnimationUtils;
import com.pratham.prathamdigital.dbclasses.DatabaseHandler;
import com.pratham.prathamdigital.fragments.Fragment_MyLibrary;
import com.pratham.prathamdigital.fragments.Fragment_Recommended;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class DashBoard_Activity extends AppCompatActivity {

    @BindView(R.id.fab_library)
    FloatingActionButton fab_library;
    @BindView(R.id.fab_recommend)
    FloatingActionButton fab_recommend;
    @BindView(R.id.fab_language)
    FloatingActionButton fab_language;

    Locale myLocale;
    String defaultLang;
    private String TAG = DashBoard_Activity.class.getSimpleName();
    DatabaseHandler gdb;
    String googleId;
    private TextToSp textToSp;
    private boolean isInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board_);
        ButterKnife.bind(this);
        gdb = new DatabaseHandler(this);
        googleId = gdb.getGoogleID();
        isInitialized = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Boolean IntroStatus = gdb.CheckIntroShownStatus(googleId);
        Log.d("IntroStatus:", IntroStatus + "");
        if (!IntroStatus) {
            // Show Intro & then set flag as shown
            gdb.SetIntroFlagTrue(1, googleId);
            Log.d("IntroStatus:", gdb.CheckIntroShownStatus(googleId) + "");
            ShowIntro();
        }
        if (!isInitialized) {
            fab_library.performClick();
            textToSp = new TextToSp(DashBoard_Activity.this);
            isInitialized = true;
        }
    }

    private void ShowIntro() {
        new MaterialTapTargetPrompt.Builder(DashBoard_Activity.this)
                .setTarget(findViewById(R.id.rl_mylibrary))
                .setPrimaryText(R.string.view_library)
                .setSecondaryText(R.string.view_contents)
                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                .setBackButtonDismissEnabled(false)
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {
                        if (state == MaterialTapTargetPrompt.STATE_DISMISSED) {
                            nextshowcase();// User has pressed the prompt target
                        }
                    }
                })
                .show();
    }

    private void nextshowcase() {
        new MaterialTapTargetPrompt.Builder(DashBoard_Activity.this)
                .setTarget(findViewById(R.id.rl_recommended))
                .setPrimaryText(R.string.view_recommended)
                .setSecondaryText(R.string.download_recommended)
                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {
                        if (state == MaterialTapTargetPrompt.STATE_DISMISSED) {
                            nextshowcase2();// User has pressed the prompt target
                        }
                    }
                })
                .show();
    }

    private void nextshowcase2() {
        new MaterialTapTargetPrompt.Builder(DashBoard_Activity.this)
                .setTarget(findViewById(R.id.rl_dash_search))
                .setPrimaryText(R.string.search_contents_from_here)
                .setSecondaryText(R.string.search_and_download)
                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {
                        if (state == MaterialTapTargetPrompt.STATE_DISMISSED) {
                        }
                    }
                })
                .show();
    }

    @OnClick(R.id.fab_library)
    public void setLibrary() {
        PD_Utility.showFragment(this, new Fragment_MyLibrary(), R.id.frame_container, null,
                Fragment_MyLibrary.class.getSimpleName());
    }

    @OnClick(R.id.fab_recommend)
    public void setRecommended() {
        PD_Utility.showFragment(this, new Fragment_Recommended(), R.id.frame_container, null,
                Fragment_Recommended.class.getSimpleName());
    }

    @OnClick(R.id.fab_search)
    public void setSearch() {
        Intent intent = new Intent(DashBoard_Activity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.nothing);
    }

    @OnClick(R.id.fab_language)
    public void setLanguage() {
        Intent intent = new Intent(DashBoard_Activity.this, Activity_Main.class);
//        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(DashBoard_Activity.this,
//                fab_language, "transition_dialog");
//        startActivityForResult(intent, 1, options.toBundle());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String language = data.getStringExtra(PD_Constant.LANGUAGE);
            }
        }
    }
}




/*
show download complete dialog using toast
set different drawable resources for dashboard vectors
test the apk
 */