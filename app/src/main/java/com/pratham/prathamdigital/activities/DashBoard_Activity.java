package com.pratham.prathamdigital.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.adapters.RV_AgeFilterAdapter;
import com.pratham.prathamdigital.adapters.RV_ContentAdapter;
import com.pratham.prathamdigital.adapters.RV_LevelAdapter;
import com.pratham.prathamdigital.adapters.RV_RecommendAdapter;
import com.pratham.prathamdigital.async.PD_ApiRequest;
import com.pratham.prathamdigital.content_playing.TextToSp;
import com.pratham.prathamdigital.fragments.Fragment_MyLibrary;
import com.pratham.prathamdigital.fragments.Fragment_Recommended;
import com.pratham.prathamdigital.interfaces.MainActivityAdapterListeners;
import com.pratham.prathamdigital.interfaces.VolleyResult_JSON;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.pratham.prathamdigital.R.id.rv_level;

public class DashBoard_Activity extends AppCompatActivity {

    @BindView(R.id.rl_mylibrary)
    RelativeLayout rl_mylibrary;
    @BindView(R.id.rl_recommended)
    RelativeLayout rl_recommended;

    private String TAG = DashBoard_Activity.class.getSimpleName();
    public static TextToSp ttspeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board_);
        ButterKnife.bind(this);
        ttspeech = new TextToSp(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!rl_mylibrary.isSelected() && !rl_recommended.isSelected()) {
            rl_mylibrary.performClick();
        }
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

    @Override
    protected void onDestroy() {
        if(ttspeech != null) {
            ttspeech.stopSpeaker();
            Log.d("tts_destroyed", "TTS Destroyed");
        }
        super.onDestroy();
    }
}
