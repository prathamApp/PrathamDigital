package com.pratham.prathamdigital.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.adapters.RV_LibraryContentAdapter;
import com.pratham.prathamdigital.adapters.RV_SubLibraryAdapter;
import com.pratham.prathamdigital.content_playing.Activity_WebView;
import com.pratham.prathamdigital.dbclasses.DatabaseHandler;
import com.pratham.prathamdigital.interfaces.MainActivityAdapterListeners;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HP on 11-08-2017.
 */

public class Fragment_MyLibrary extends Fragment implements MainActivityAdapterListeners {

    private static final String TAG = Fragment_MyLibrary.class.getSimpleName();
    @BindView(R.id.rv_ages_filter)
    RecyclerView rv_library;
    @BindView(R.id.rv_recommend_content)
    RecyclerView rv_sub_library;

    RV_LibraryContentAdapter libraryContentAdapter;
    RV_SubLibraryAdapter subLibraryAdapter;
    private AlertDialog dialog = null;
    private DatabaseHandler db;
    private ArrayList<Modal_ContentDetail> downloadContents = new ArrayList<>();
    private ArrayList<Modal_ContentDetail> subContents = new ArrayList<>();
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
        db = new DatabaseHandler(getActivity());
        dialog = PD_Utility.showLoader(getActivity());
        isInitialized = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        downloadContents = db.Get_Contents(PD_Constant.TABLE_PARENT, 0);
        if (downloadContents.size() > 0) {
            PD_Utility.DEBUG_LOG(1, TAG, "db_list_size::" + downloadContents.size());
            PD_Utility.DEBUG_LOG(1, TAG, "db_nodelist_size::" + downloadContents.size());
            if (!isInitialized) {
                libraryContentAdapter = new RV_LibraryContentAdapter(getActivity(), this, downloadContents);
                rv_library.getViewTreeObserver().addOnPreDrawListener(preDrawListenerBrowse);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                rv_library.setLayoutManager(layoutManager);
                rv_library.setAdapter(libraryContentAdapter);
                isInitialized = true;
            } else {
                rv_library.getViewTreeObserver().addOnPreDrawListener(preDrawListenerBrowse);
                libraryContentAdapter.notifyDataSetChanged();
            }
        }
    }

    ViewTreeObserver.OnPreDrawListener preDrawListenerBrowse = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            rv_library.getViewTreeObserver().removeOnPreDrawListener(this);
            for (int i = 0; i < rv_library.getChildCount(); i++) {
                View view = rv_library.getChildAt(i);
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
            rv_sub_library.getViewTreeObserver().removeOnPreDrawListener(this);
            for (int i = 0; i < rv_sub_library.getChildCount(); i++) {
                View view = rv_sub_library.getChildAt(i);
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
        libraryContentAdapter.setSelectedIndex(position);
        subContents = db.Get_Contents(PD_Constant.TABLE_CHILD, downloadContents.get(position).getNodeid());
        if (subLibraryAdapter == null) {
            subLibraryAdapter = new RV_SubLibraryAdapter(getActivity(), this, subContents);
            LinearLayoutManager layoutManager3 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            rv_sub_library.getViewTreeObserver().addOnPreDrawListener(preDrawListenerRecommend);
            rv_sub_library.setLayoutManager(layoutManager3);
            rv_sub_library.setAdapter(subLibraryAdapter);
        } else {
            subLibraryAdapter.updateData(subContents);
        }
    }

    @Override
    public void contentButtonClicked(int position) {
        if (subContents.get(position).getNodetype().equalsIgnoreCase("Resource")) {
            if (subContents.get(position).getResourcetype().equalsIgnoreCase("Game")) {
                Intent intent = new Intent(getActivity(), Activity_WebView.class);
                //path to /data/data/yourapp/app_data/dirName
//                ContextWrapper cw = new ContextWrapper(getActivity());
                File directory = getActivity().getDir("PrathamGame", Context.MODE_PRIVATE);
//                File filepath = new File(directory, fileName);
                Log.d("directory_path:::", directory.getAbsolutePath());
                Log.d("game_filepath:::", directory.getAbsolutePath() + "/" + subContents.get(position).getResourcepath());
                Log.d("game_filepath:::", new StringTokenizer(subContents.get(position).getResourcepath(), "/").nextToken() + "/");
                intent.putExtra("index_path", directory.getAbsolutePath() + "/" + subContents.get(position).getResourcepath());
                intent.putExtra("path", directory.getAbsolutePath() + "/" +
                        new StringTokenizer(subContents.get(position).getResourcepath(), "/").nextToken() + "/");
                Runtime rs = Runtime.getRuntime();
                rs.freeMemory();
                rs.gc();
                rs.freeMemory();
                getActivity().startActivity(intent);
            }
        } else {
            ArrayList<Modal_ContentDetail> list = db.Get_Contents(PD_Constant.TABLE_CHILD, subContents.get(position).getNodeid());
            subContents.clear();
            subContents.addAll(list);
            Log.d("sub_content_size::", subContents.size() + "");
            subLibraryAdapter.updateData(subContents);
        }
    }

    @Override
    public void levelButtonClicked(int position) {

    }

    @Override
    public void downloadClick(int position, View holder) {

    }

    @Override
    public void downloadComplete(int position) {

    }
}
