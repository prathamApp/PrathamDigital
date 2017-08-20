package com.pratham.prathamdigital.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.transition.ArcMotion;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.async.ImageDownload;
import com.pratham.prathamdigital.async.PD_ApiRequest;
import com.pratham.prathamdigital.async.ZipDownloader;
import com.pratham.prathamdigital.content_playing.TextToSp;
import com.pratham.prathamdigital.custom.custom_fab.FloatingActionButton;
import com.pratham.prathamdigital.custom.morphing.MorphDialogToFab;
import com.pratham.prathamdigital.custom.morphing.MorphFabToDialog;
import com.pratham.prathamdigital.custom.progress_indicators.CircleProgressView;
import com.pratham.prathamdigital.dbclasses.DatabaseHandler;
import com.pratham.prathamdigital.interfaces.ProgressUpdate;
import com.pratham.prathamdigital.interfaces.VolleyResult_JSON;
import com.pratham.prathamdigital.models.Modal_DownloadContent;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HP on 18-08-2017.
 */

public class Activity_DownloadDialog extends AppCompatActivity implements VolleyResult_JSON, ProgressUpdate {

    private static final String TAG = Activity_DownloadDialog.class.getSimpleName();
    @BindView(R.id.card_content)
    ViewGroup card_content;
    @BindView(R.id.d_content_img)
    ImageView d_content_img;
    //    @BindView(R.id.d_progressbar)
//    CircleProgressView d_progressbar;
    @BindView(R.id.fab_download2)
    FloatingActionButton fab_download2;
    @BindView(R.id.d_name)
    TextView d_name;
    private Modal_DownloadContent download_content;
    private DatabaseHandler db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_dialog);
        ButterKnife.bind(this);
        db = new DatabaseHandler(this);
        card_content.setTransitionName(getIntent().getExtras().getString("transition_name"));
        setupEnterTransitions();
        if (PD_Utility.isInternetAvailable(this)) {
            d_name.setText(getIntent().getExtras().getString("title"));
            Picasso.with(this).load(getIntent().getExtras().getString("image")).into(d_content_img);
            new PD_ApiRequest(this, this).getDataVolley("DOWNLOAD",
                    PD_Constant.URL.DOWNLOAD_RESOURCE.toString() + getIntent().getExtras().getInt("ID"));
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            setResult(Activity.RESULT_CANCELED, intent);
            finishAfterTransition();
        }
    }

    private void setupEnterTransitions() {
        ArcMotion arcMotion = new ArcMotion();
        arcMotion.setMinimumHorizontalAngle(50f);
        arcMotion.setMinimumVerticalAngle(50f);

        Interpolator easeInOut = AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in);

        MorphFabToDialog sharedEnter = new MorphFabToDialog();
        sharedEnter.setPathMotion(arcMotion);
        sharedEnter.setInterpolator(easeInOut);

        MorphDialogToFab sharedReturn = new MorphDialogToFab();
        sharedReturn.setPathMotion(arcMotion);
        sharedReturn.setInterpolator(easeInOut);

        if (card_content != null) {
            sharedEnter.addTarget(card_content);
            sharedReturn.addTarget(card_content);
        }
        getWindow().setSharedElementEnterTransition(sharedEnter);
        getWindow().setSharedElementReturnTransition(sharedReturn);
    }

    @Override
    public void notifySuccess(String requestType, String response) {
        try {
            Gson gson = new Gson();
            if (requestType.equalsIgnoreCase("DOWNLOAD")) {
                JSONObject jsonObject = new JSONObject(response);
                download_content = gson.fromJson(jsonObject.toString(), Modal_DownloadContent.class);
                PD_Utility.DEBUG_LOG(1, TAG, "nodelist_length:::" + download_content.getNodelist().size());
                PD_Utility.DEBUG_LOG(1, TAG, "foldername:::" + download_content.getFoldername());
                String fileName = download_content.getDownloadurl().substring(download_content.getDownloadurl().lastIndexOf('/') + 1);
                PD_Utility.DEBUG_LOG(1, TAG, "filename:::" + fileName);
                PowerManager pm = (PowerManager) Activity_DownloadDialog.this.getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
                if (download_content.getDownloadurl().length() > 0) {
                    new ZipDownloader(Activity_DownloadDialog.this, Activity_DownloadDialog.this, download_content.getDownloadurl(),
                            download_content.getFoldername(), fileName, wl);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifyError(String requestType, VolleyError error) {
        try {
            Log.d("response:::", "requestType:: " + requestType);
            Log.d("error:::", error.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProgressUpdate(int progress) {
        fab_download2.setProgress(progress, false);
        if (progress == 100) {
            Intent intent = new Intent();
            intent.putExtra("position", getIntent().getExtras().getInt("position"));
            setResult(Activity.RESULT_OK, intent);
            finishAfterTransition();
        }
    }

    @Override
    public void onZipDownloaded(boolean isDownloaded) {
        if (isDownloaded) {
            for (int i = 0; i < download_content.getNodelist().size(); i++) {
                String fileName = download_content.getNodelist().get(i).getNodeserverimage()
                        .substring(download_content.getNodelist().get(i).getNodeserverimage().lastIndexOf('/') + 1);
                new ImageDownload(Activity_DownloadDialog.this, fileName)
                        .execute(download_content.getNodelist().get(i).getNodeserverimage());
            }
            addContentToDatabase(download_content);
        }
    }

    private void addContentToDatabase(Modal_DownloadContent download_content) {
        ArrayList<String> p_ids = db.getDownloadContentID(PD_Constant.TABLE_PARENT);
        ArrayList<String> c_ids = db.getDownloadContentID(PD_Constant.TABLE_CHILD);
        for (int i = 0; i < download_content.getNodelist().size(); i++) {
            if (i == 0) {
                if (!p_ids.contains(String.valueOf(download_content.getNodelist().get(i).getNodeid())))
                    db.Add_Content(PD_Constant.TABLE_PARENT, download_content.getNodelist().get(i));
            } else {
                if (!c_ids.contains(String.valueOf(download_content.getNodelist().get(i).getNodeid())))
                    db.Add_Content(PD_Constant.TABLE_CHILD, download_content.getNodelist().get(i));
            }
        }
        db.Add_DOownloadedFileDetail(download_content.getNodelist().get(download_content.getNodelist().size() - 1));
    }

    @Override
    public void lengthOfTheFile(int length) {

    }
}
