package com.pratham.prathamdigital.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.activities.DashBoard_Activity;
import com.pratham.prathamdigital.adapters.RV_AgeFilterAdapter;
import com.pratham.prathamdigital.adapters.RV_RecommendAdapter;
import com.pratham.prathamdigital.async.PD_ApiRequest;
import com.pratham.prathamdigital.interfaces.MainActivityAdapterListeners;
import com.pratham.prathamdigital.interfaces.VolleyResult_JSON;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HP on 11-08-2017.
 */

public class Fragment_Recommended extends Fragment implements MainActivityAdapterListeners, VolleyResult_JSON {

    private static final String TAG = Fragment_Recommended.class.getSimpleName();
    @BindView(R.id.rv_ages_filter)
    RecyclerView rv_ages_filter;
    @BindView(R.id.rv_recommend_content)
    RecyclerView rv_recommend_content;

    RV_AgeFilterAdapter ageFilterAdapter;
    RV_RecommendAdapter rv_recommendAdapter;
    String[] age = {"Age\n3-6", "Age\n6-10", "Age\n8-14", "Age\n14+"};
    int[] age_id = {4, 5, 1, 2};
    int[] childs = {R.drawable.ic_baby_wrapped, R.drawable.ic_boy_wrapped, R.drawable.ic_10year_boy_wrapped, R.drawable.ic_adult_boy_wrapped};
    private AlertDialog dialog;
    private ArrayList<Modal_ContentDetail> arrayList_content = new ArrayList<>();
    private boolean isInitialized;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recommended, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        dialog = PD_Utility.showLoader(getActivity());
        isInitialized = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isInitialized) {
            ageFilterAdapter = new RV_AgeFilterAdapter(getActivity(), this, age, childs);
            rv_ages_filter.getViewTreeObserver().addOnPreDrawListener(preDrawListenerBrowse);
            isInitialized = true;
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_ages_filter.setLayoutManager(layoutManager);
        //inflating the ages filter recycler view
        rv_ages_filter.setAdapter(ageFilterAdapter);
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
    ViewTreeObserver.OnPreDrawListener preDrawListenerRecommend = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            rv_recommend_content.getViewTreeObserver().removeOnPreDrawListener(this);
            for (int i = 0; i < rv_recommend_content.getChildCount(); i++) {
                View view = rv_recommend_content.getChildAt(i);
                view.animate().cancel();
                view.setTranslationX(100);
                view.setAlpha(0);
                view.animate().alpha(1.0f).translationX(0).setDuration(300).setStartDelay(i * 50);
            }
            return true;
        }
    };

    @Override
    public void browserButtonClicked(final int position) {
        ageFilterAdapter.setSelectedIndex(position);
        showDialog();
        if (PD_Utility.isInternetAvailable(getActivity())) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new PD_ApiRequest(getActivity(), Fragment_Recommended.this).getDataVolley("BROWSE",
                            PD_Constant.URL.BROWSE_BY_ID.toString() + age_id[position]);
                }
            }, 2000);
        }
    }

    @Override
    public void contentButtonClicked(int position) {

    }

    @Override
    public void levelButtonClicked(int position) {

    }

    @Override
    public void downloadClick(int position, RecyclerView.ViewHolder holder) {

    }

    @Override
    public void downloadComplete(int position) {

    }

    private void showDialog() {
        if (dialog == null)
            dialog = PD_Utility.showLoader(getActivity());
        dialog.show();
    }

    @Override
    public void notifySuccess(String requestType, String response) {
        try {
            Log.d("response:::", response);
            Log.d("response:::", "requestType:: " + requestType);
            Gson gson = new Gson();
            if (requestType.equalsIgnoreCase("BROWSE")) {
                arrayList_content.clear();
                Type listType = new TypeToken<ArrayList<Modal_ContentDetail>>() {
                }.getType();
                arrayList_content = gson.fromJson(response, listType);
                PD_Utility.DEBUG_LOG(1, TAG, "content_length:::" + arrayList_content.size());
                //inflating the recommended content recycler view
                if (rv_recommendAdapter == null) {
                    LinearLayoutManager layoutManager3 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    rv_recommend_content.setLayoutManager(layoutManager3);
                    rv_recommend_content.getViewTreeObserver().addOnPreDrawListener(preDrawListenerRecommend);
                    rv_recommendAdapter = new RV_RecommendAdapter(getActivity(), this, arrayList_content);
                    rv_recommend_content.setAdapter(rv_recommendAdapter);
                } else {
                    rv_recommendAdapter.updateData(arrayList_content);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }

    @Override
    public void notifyError(String requestType, VolleyError error) {
        try {
            Log.d("response:::", "requestType:: " + requestType);
            Log.d("error:::", error.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }
}
