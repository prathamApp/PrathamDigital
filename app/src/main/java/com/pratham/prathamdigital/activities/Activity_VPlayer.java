package com.pratham.prathamdigital.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.transition.ArcMotion;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.morphing.MorphDialogToFab;
import com.pratham.prathamdigital.custom.morphing.MorphFabToDialog;
import com.pratham.prathamdigital.custom.video_player.EasyVideoCallback;
import com.pratham.prathamdigital.custom.video_player.EasyVideoPlayer;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HP on 19-08-2017.
 */

public class Activity_VPlayer extends AppCompatActivity implements EasyVideoCallback {

    @BindView(R.id.rl_easy_title)
    ViewGroup rl_easy_title;
    @BindView(R.id.easy_vp)
    EasyVideoPlayer easy_vp;

    private static final String TEST_URL = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vplayer);
        ButterKnife.bind(this);
//        setupEnterTransitions();
        easy_vp.setCallback(this);
        easy_vp.setSource(Uri.parse(TEST_URL));
        easy_vp.start();
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

        if (rl_easy_title != null) {
            sharedEnter.addTarget(rl_easy_title);
            sharedReturn.addTarget(rl_easy_title);
        }
        getWindow().setSharedElementEnterTransition(sharedEnter);
        getWindow().setSharedElementReturnTransition(sharedReturn);
    }

    @Override
    protected void onPause() {
        super.onPause();
        easy_vp.pause();
    }

    @Override
    public void onStarted(EasyVideoPlayer player) {

    }

    @Override
    public void onPaused(EasyVideoPlayer player) {
        easy_vp.pause();
    }

    @Override
    public void onPreparing(EasyVideoPlayer player) {

    }

    @Override
    public void onPrepared(EasyVideoPlayer player) {

    }

    @Override
    public void onBuffering(int percent) {
    }

    @Override
    public void onError(EasyVideoPlayer player, Exception e) {

    }

    @Override
    public void onCompletion(EasyVideoPlayer player) {
        easy_vp.stop();
        easy_vp.release();
        setResult(RESULT_CANCELED);
        finishAfterTransition();
    }

    @Override
    public void onRetry(EasyVideoPlayer player, Uri source) {
        easy_vp.reset();
    }

    @Override
    public void onSubmit(EasyVideoPlayer player, Uri source) {
        easy_vp.stop();
        easy_vp.release();
        setResult(RESULT_CANCELED);
        finishAfterTransition();
    }

    @Override
    public void onClickVideoFrame(EasyVideoPlayer player) {
        easy_vp.toggleControls();
    }
}
