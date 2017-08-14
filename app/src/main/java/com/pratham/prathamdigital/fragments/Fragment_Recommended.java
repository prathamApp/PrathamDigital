package com.pratham.prathamdigital.fragments;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.activities.DashBoard_Activity;
import com.pratham.prathamdigital.activities.MainActivity;
import com.pratham.prathamdigital.adapters.RV_AgeFilterAdapter;
import com.pratham.prathamdigital.adapters.RV_ContentAdapter;
import com.pratham.prathamdigital.adapters.RV_RecommendAdapter;
import com.pratham.prathamdigital.async.ImageDownload;
import com.pratham.prathamdigital.async.PD_ApiRequest;
import com.pratham.prathamdigital.async.ZipDownloader;
import com.pratham.prathamdigital.dbclasses.DatabaseHandler;
import com.pratham.prathamdigital.interfaces.MainActivityAdapterListeners;
import com.pratham.prathamdigital.interfaces.PermissionResult;
import com.pratham.prathamdigital.interfaces.ProgressUpdate;
import com.pratham.prathamdigital.interfaces.VolleyResult_JSON;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_DownloadContent;
import com.pratham.prathamdigital.util.FragmentManagePermission;
import com.pratham.prathamdigital.util.NetworkChangeReceiver;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;
import com.pratham.prathamdigital.util.PermissionUtils;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HP on 11-08-2017.
 */

public class Fragment_Recommended extends FragmentManagePermission implements MainActivityAdapterListeners,
        VolleyResult_JSON, ProgressUpdate, Observer {

    private static final String TAG = Fragment_Recommended.class.getSimpleName();
    @BindView(R.id.rv_ages_filter)
    RecyclerView rv_ages_filter;
    @BindView(R.id.rv_recommend_content)
    RecyclerView rv_recommend_content;
    @BindView(R.id.rl_not_connected)
    RelativeLayout rl_not_connected;
    @BindView(R.id.rl_connected)
    RelativeLayout rl_connected;
    @BindView(R.id.img_no_connection)
    ImageView img_no_connection;

    RV_AgeFilterAdapter ageFilterAdapter;
    RV_RecommendAdapter rv_recommendAdapter;
    String[] age = {"Age\n3-6", "Age\n6-10", "Age\n8-14", "Age\n14+"};
    int[] age_id = {4, 5, 1, 2};
    int[] childs = {R.drawable.ic_baby_wrapped, R.drawable.ic_boy_wrapped, R.drawable.ic_10year_boy_wrapped, R.drawable.ic_adult_boy_wrapped};
    private AlertDialog dialog;
    private ArrayList<Modal_ContentDetail> arrayList_content = new ArrayList<>();
    private boolean isInitialized;
    private Modal_DownloadContent download_content;
    private DatabaseHandler db;

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
        db = new DatabaseHandler(getActivity());
        isInitialized = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        NetworkChangeReceiver.getObservable().addObserver(this);
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
    public void contentButtonClicked(final int position) {
        showDialog();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new PD_ApiRequest(getActivity(), Fragment_Recommended.this).getDataVolley("BROWSE",
                        PD_Constant.URL.BROWSE_BY_ID.toString() + arrayList_content.get(position).getNodeid());
            }
        }, 2000);

    }

    @Override
    public void levelButtonClicked(int position) {

    }

    @Override
    public void downloadClick(final int position, final RecyclerView.ViewHolder holder) {
        if (!isPermissionsGranted(getActivity(), new String[]{PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE,
                PermissionUtils.Manifest_READ_EXTERNAL_STORAGE})) {
            askCompactPermissions(new String[]{PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE,
                    PermissionUtils.Manifest_READ_EXTERNAL_STORAGE}, new PermissionResult() {
                @Override
                public void permissionGranted() {
                    rv_recommendAdapter.setSelectedIndex(position, (RV_RecommendAdapter.ViewHolder) holder);
                    if (PD_Utility.isInternetAvailable(getActivity())) {
                        new PD_ApiRequest(getActivity(), Fragment_Recommended.this).getDataVolley("DOWNLOAD",
                                PD_Constant.URL.DOWNLOAD_RESOURCE.toString() + arrayList_content.get(position).getNodeid());
                    } else {
                        Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void permissionDenied() {

                }

                @Override
                public void permissionForeverDenied() {
                    Toast.makeText(getActivity(), "Provide Permission for storage first", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            rv_recommendAdapter.setSelectedIndex(position, (RV_RecommendAdapter.ViewHolder) holder);
            if (PD_Utility.isInternetAvailable(getActivity())) {
                new PD_ApiRequest(getActivity(), Fragment_Recommended.this).getDataVolley("DOWNLOAD",
                        PD_Constant.URL.DOWNLOAD_RESOURCE.toString() + arrayList_content.get(position).getNodeid());
            } else {
                Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void downloadComplete(int position) {
        arrayList_content.remove(position);
        rv_recommendAdapter.notifyItemRemoved(position);
        rv_recommendAdapter.notifyItemRangeChanged(position, arrayList_content.size());
        rv_recommendAdapter.updateData(arrayList_content);
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
                //retrieving ids of downloaded contents from database
                ArrayList<String> downloaded_ids = new ArrayList<>();
                downloaded_ids = db.getDownloadContentID();
                if (downloaded_ids.size() > 0) {
                    Log.d("contents_downloaded::", downloaded_ids.size() + "");
                    for (int i = 0; i < downloaded_ids.size(); i++) {
                        for (int j = 0; j < arrayList_content.size(); j++) {
                            if (arrayList_content.get(j).getResourceid().equalsIgnoreCase(downloaded_ids.get(i))) {
                                Log.d("contents_downloaded::", "downloaded content removed");
                                arrayList_content.remove(j);
                            }
                        }
                    }
                }
                //inflating the recommended content recycler view
                if (rv_recommendAdapter == null) {
                    LinearLayoutManager layoutManager3 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    rv_recommend_content.setLayoutManager(layoutManager3);
                    rv_recommend_content.getViewTreeObserver().addOnPreDrawListener(preDrawListenerRecommend);
                    rv_recommendAdapter = new RV_RecommendAdapter(getActivity(), this, arrayList_content);
                    rv_recommend_content.setAdapter(rv_recommendAdapter);
                } else {
                    rv_recommend_content.getViewTreeObserver().addOnPreDrawListener(preDrawListenerRecommend);
                    rv_recommendAdapter.updateData(arrayList_content);
                }
            } else if (requestType.equalsIgnoreCase("DOWNLOAD")) {
                JSONObject jsonObject = new JSONObject(response);
                download_content = gson.fromJson(jsonObject.toString(), Modal_DownloadContent.class);
                PD_Utility.DEBUG_LOG(1, TAG, "nodelist_length:::" + download_content.getNodelist().size());
                PD_Utility.DEBUG_LOG(1, TAG, "foldername:::" + download_content.getFoldername());
                String fileName = download_content.getDownloadurl().substring(download_content.getDownloadurl().lastIndexOf('/') + 1);
                PD_Utility.DEBUG_LOG(1, TAG, "filename:::" + fileName);
                if (download_content.getDownloadurl().length() > 0) {
                    new ZipDownloader(getActivity(), Fragment_Recommended.this, download_content.getDownloadurl(),
                            download_content.getFoldername(), fileName);
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

    @Override
    public void onProgressUpdate(int progress) {
        rv_recommendAdapter.setProgress(progress);
    }

    @Override
    public void onZipDownloaded(boolean isDownloaded) {
        if (isDownloaded) {
            for (int i = 0; i < download_content.getNodelist().size(); i++) {
                String fileName = download_content.getNodelist().get(i).getNodeserverimage()
                        .substring(download_content.getNodelist().get(i).getNodeserverimage().lastIndexOf('/') + 1);
                new ImageDownload(getActivity(), fileName).execute(download_content.getNodelist().get(i).getNodeserverimage());
            }
            db.Add_Content(download_content);
            db.Add_DOownloadedFileDetail(download_content.getNodelist().get(download_content.getNodelist().size() - 1));
        }
    }

    @Override
    public void lengthOfTheFile(int length) {
        Log.d("lenghtOfFile::", length + "");
    }

    @Override
    public void update(Observable observable, Object o) {
        Log.d("update:::", "called");
        if (!PD_Utility.isInternetAvailable(getActivity())) {
            rl_connected.setVisibility(View.GONE);
            rl_not_connected.setVisibility(View.VISIBLE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                AnimatedVectorDrawable avd = (AnimatedVectorDrawable)
                        getActivity().getDrawable(R.drawable.avd_no_connection);
                img_no_connection.setImageDrawable(avd);
            } else {
                img_no_connection.setImageResource(R.drawable.ic_no_connection_fix_wrapped);
            }
            Drawable animation = img_no_connection.getDrawable();
            if (animation instanceof Animatable) {
                ((Animatable) animation).start();
            }
        } else {
            rl_connected.setVisibility(View.VISIBLE);
            rl_not_connected.setVisibility(View.GONE);
            onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        NetworkChangeReceiver.getObservable().deleteObserver(this);
    }
}
