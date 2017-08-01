package com.pratham.prathamdigital.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public class PD_SharedPreference {

    /**
     * Klik_SharedPreferences Class is used to maintain shared preferences
     */

	/*
     * Klik_SharedPreferences Members Declarations
	 */
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private Editor mEditor;

    private String str_PrefName = "PD_Pref";

    /**
     * Klik_SharedPreferences Constructor Implementation
     */
    public PD_SharedPreference(Context context,
                               OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener) {
        this.mContext = context;

        mSharedPreferences = mContext.getSharedPreferences(str_PrefName,
                Context.MODE_PRIVATE);
        if (mOnSharedPreferenceChangeListener != null) {
            mSharedPreferences
                    .registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);
        }
    }

    /**
     * This method is used to store String value in SharedPreferences
     */

    public void putStringValue(String editorkey, String editorvalue) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(editorkey, editorvalue);
        mEditor.commit();
    }

    /**
     * This method is used to store int value in SharedPreferences
     */

    public void putIntValue(String editorkey, int editorvalue) {
        mEditor = mSharedPreferences.edit();
        mEditor.putInt(editorkey, editorvalue);
        mEditor.commit();
    }

    /**
     * This method is used to store boolean value in SharedPreferences
     */

    public void putBooleanValue(String editorkey, boolean editorvalue) {
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(editorkey, editorvalue);
        mEditor.commit();
    }

    /**
     * This method is used to get String value from SharedPreferences
     *
     * @return String PrefValue
     */

    public String getStringValue(String editorkey, String defValue) {
        String PrefValue = mSharedPreferences.getString(editorkey, defValue);

        return PrefValue;
    }

    /**
     * This method is used to get int value from SharedPreferences
     *
     * @return int PrefValue
     */

    public int getIntValue(String editorkey, int defValue) {
        int PrefValue = mSharedPreferences.getInt(editorkey, defValue);

        return PrefValue;

    }

    /**
     * This method is used to get boolean value from SharedPreferences
     *
     * @return boolean PrefValue
     */

    public boolean getBooleanValue(String editorkey, boolean defValue) {
        boolean PrefValue = mSharedPreferences.getBoolean(editorkey, defValue);

        return PrefValue;

    }

    public void delete() {

        mSharedPreferences.edit().clear().commit();

    }

    /**
     * Method to delete a given value from the preference.
     *
     * @param str_key - key of the value you want erase from preference.
     */

    public void deleteKey(String str_key) {
        mEditor = mSharedPreferences.edit();
        mEditor.remove(str_key).commit();
    }

}
