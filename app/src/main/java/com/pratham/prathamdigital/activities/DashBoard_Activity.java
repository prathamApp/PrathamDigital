package com.pratham.prathamdigital.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.pratham.prathamdigital.MainActivity;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.adapters.RV_AgeFilterAdapter;
import com.pratham.prathamdigital.interfaces.MainActivityAdapterListeners;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DashBoard_Activity extends AppCompatActivity implements MainActivityAdapterListeners {

    @BindView(R.id.rv_ages_filter)
    RecyclerView rv_ages_filter;
    @BindView(R.id.rv_sub_content)
    RecyclerView rv_sub_content;
    @BindView(R.id.rl_mylibrary)
    RelativeLayout rl_mylibrary;
    @BindView(R.id.rl_recommended)
    RelativeLayout rl_recommended;

    RV_AgeFilterAdapter ageFilterAdapter;
    String[] age = {"Age\n3-6", "Age\n6-10", "Age\n8-14", "Age\n14+"};
    int[] childs = {R.drawable.ic_baby_wrapped, R.drawable.ic_boy_wrapped, R.drawable.ic_10year_boy_wrapped, R.drawable.ic_adult_boy_wrapped};
    private boolean isInitialized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board_);
        ButterKnife.bind(this);
        isInitialized = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isInitialized) {
            ageFilterAdapter = new RV_AgeFilterAdapter(this, this, age, childs);
            rv_ages_filter.getViewTreeObserver().addOnPreDrawListener(preDrawListenerBrowse);
            isInitialized = true;
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_ages_filter.setLayoutManager(layoutManager);

        //inflating the ages filter recycler view
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rv_ages_filter.setAdapter(ageFilterAdapter);
            }
        }, 500);

    }

    ViewTreeObserver.OnPreDrawListener preDrawListenerBrowse = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            rv_ages_filter.getViewTreeObserver().removeOnPreDrawListener(this);
            for (int i = 0; i < rv_ages_filter.getChildCount(); i++) {
                View view = rv_ages_filter.getChildAt(i);
                view.animate().cancel();
                view.setTranslationX(100);
                view.setAlpha(0);
                view.animate().alpha(1.0f).translationX(0).setDuration(300).setStartDelay(i * 50);
            }
            return true;
        }
    };

    @Override
    public void browserButtonClicked(int position) {
        ageFilterAdapter.setSelectedIndex(position);
    }

    @Override
    public void contentButtonClicked(int position) {

    }

    @OnClick(R.id.rl_mylibrary)
    public void setRl_mylibrary() {
        rl_mylibrary.setSelected(true);
        rl_recommended.setSelected(false);
    }

    @OnClick(R.id.rl_recommended)
    public void setRl_recommended() {
        rl_mylibrary.setSelected(false);
        rl_recommended.setSelected(true);
    }

    @OnClick(R.id.rl_dash_search)
    public void setRlDashSearch() {
        Intent intent = new Intent(DashBoard_Activity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.nothing);
    }
}
