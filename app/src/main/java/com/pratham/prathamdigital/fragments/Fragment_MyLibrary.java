package com.pratham.prathamdigital.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.util.PD_Utility;

import butterknife.ButterKnife;

/**
 * Created by HP on 11-08-2017.
 */

public class Fragment_MyLibrary extends Fragment {

    private AlertDialog dialog=null;
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
    }
}
