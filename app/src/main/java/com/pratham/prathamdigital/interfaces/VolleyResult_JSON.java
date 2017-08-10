package com.pratham.prathamdigital.interfaces;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by HP on 30-12-2016.
 */

public interface VolleyResult_JSON {
    public void notifySuccess(String requestType, String response);

    public void notifyError(String requestType, VolleyError error);
}
