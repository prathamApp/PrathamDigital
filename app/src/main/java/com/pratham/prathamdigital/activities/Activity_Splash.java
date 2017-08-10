package com.pratham.prathamdigital.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.util.PD_Utility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Activity_Splash extends AppCompatActivity {

    @BindView(R.id.btn_google_login)
    Button btn_google_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__splash);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation expandIn = AnimationUtils.loadAnimation(Activity_Splash.this, R.anim.pop_in);
                btn_google_login.setVisibility(View.VISIBLE);
                btn_google_login.startAnimation(expandIn);
            }
        }, 2000);
    }

    @OnClick(R.id.btn_google_login)
    public void next() {
        PD_Utility.showLoader(Activity_Splash.this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Activity_Splash.this, DashBoard_Activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.nothing);
            }
        }, 3000);
    }
}
