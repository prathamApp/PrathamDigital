package com.pratham.prathamdigital.interfaces;

import android.view.View;

import com.android.volley.VolleyError;
import com.pratham.prathamdigital.adapters.RV_ContentAdapter;
import com.pratham.prathamdigital.custom.progress_indicators.ProgressLayout;

import org.json.JSONObject;

/**
 * Created by HP on 30-12-2016.
 */

public interface MainActivityAdapterListeners {
    public void browserButtonClicked(int position);
    public void contentButtonClicked(int position);
    public void levelButtonClicked(int position);
    public void downloadClick(int position, RV_ContentAdapter.ViewHolder holder);
}
