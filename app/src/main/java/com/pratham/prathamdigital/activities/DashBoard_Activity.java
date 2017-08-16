package com.pratham.prathamdigital.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.dbclasses.GoogleDBHelper;
import com.pratham.prathamdigital.fragments.Fragment_MyLibrary;
import com.pratham.prathamdigital.fragments.Fragment_Recommended;
import com.pratham.prathamdigital.util.PD_Utility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class DashBoard_Activity extends AppCompatActivity {

    @BindView(R.id.rl_mylibrary)
    RelativeLayout rl_mylibrary;
    @BindView(R.id.rl_recommended)
    RelativeLayout rl_recommended;

    private String TAG = DashBoard_Activity.class.getSimpleName();
    GoogleDBHelper gdb;
    String googleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board_);
        ButterKnife.bind(this);
        gdb = new GoogleDBHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundle = getIntent().getExtras();
        googleId = bundle.getString("GoogleID");
        Boolean IntroStatus = gdb.CheckIntroShownStatus(googleId);
        if (!IntroStatus) {
            // Show Intro & then set flag as shown
            gdb.SetFlagTrue(1, googleId);
            ShowIntro();
        }
        if (!rl_mylibrary.isSelected() && !rl_recommended.isSelected()) {
            rl_mylibrary.performClick();
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

    @OnClick(R.id.rl_mylibrary)
    public void setRl_mylibrary() {
        rl_mylibrary.setSelected(true);
        rl_recommended.setSelected(false);
        PD_Utility.showFragment(this, new Fragment_MyLibrary(), R.id.frame_container, null,
                Fragment_MyLibrary.class.getSimpleName());
    }

    @OnClick(R.id.rl_recommended)
    public void setRl_recommended() {
        rl_mylibrary.setSelected(false);
        rl_recommended.setSelected(true);
        PD_Utility.showFragment(this, new Fragment_Recommended(), R.id.frame_container, null,
                Fragment_Recommended.class.getSimpleName());
    }

    @OnClick(R.id.rl_dash_search)
    public void setRlDashSearch() {
        Intent intent = new Intent(DashBoard_Activity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.nothing);
    }

    @Override
    public void onBackPressed() {
    }
}
