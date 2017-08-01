package com.pratham.prathamdigital;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.pratham.prathamdigital.adapters.RV_BrowseAdapter;
import com.pratham.prathamdigital.adapters.RV_ContentAdapter;
import com.pratham.prathamdigital.adapters.RV_LevelAdapter;
import com.pratham.prathamdigital.interfaces.MainActivityAdapterListeners;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainActivityAdapterListeners {

    @BindView(R.id.rv_browse_contents)
    RecyclerView rv_browse_contents;
    @BindView(R.id.rv_contents)
    RecyclerView rv_contents;
    @BindView(R.id.rv_level)
    RecyclerView rv_level;

    RV_BrowseAdapter rv_browseAdapter;
    RV_ContentAdapter rv_contentAdapter;
    RV_LevelAdapter rv_levelAdapter;
    String[] name = {"khelbadi", "goodmorning", "hello", "games", "maths", "english"};
    String[] sub_content = {"khelbadi1", "goodmorning1", "hello1", "games1", "maths1", "english1"};
    String[] levels = {"level1", "level2", "level3", "level4"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Initializing the adapters
        rv_browseAdapter = new RV_BrowseAdapter(this, this, name);
        rv_contentAdapter = new RV_ContentAdapter(this, this, sub_content);
        rv_levelAdapter = new RV_LevelAdapter(this, this, levels);

        //Deifining the layouts for each recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_browse_contents.setLayoutManager(layoutManager);
        LinearLayoutManager layoutManager2 = new GridLayoutManager(this, 3);
        rv_contents.setLayoutManager(layoutManager2);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_level.setLayoutManager(layoutManager3);

        //inflating the browser content recycler view
        rv_browse_contents.setAdapter(rv_browseAdapter);
    }

    @Override
    public void browserButtonClicked(int position) {
        rv_browseAdapter.setSelectedIndex(position);

        //inflating the content recycler view
        rv_contents.setAdapter(rv_contentAdapter);
    }

    @Override
    public void contentButtonClicked(int position) {
        rv_contentAdapter.setSelectedIndex(position);
//        rv_contentAdapter.notifyItemRemoved(4);

        //inflating the content recycler view
        rv_level.setAdapter(rv_levelAdapter);
    }
}
