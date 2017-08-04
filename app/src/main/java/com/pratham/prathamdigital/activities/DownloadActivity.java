package com.pratham.prathamdigital.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.async.ZipDownloader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DownloadActivity extends AppCompatActivity {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.d_button)
    public void downloadContent(){
        new ZipDownloader(progressBar); //"http://hlearning.openiscool.org/content/games/AwazChitraH.zip"
    }
}
