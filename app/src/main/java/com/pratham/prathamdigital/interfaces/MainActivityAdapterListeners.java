package com.pratham.prathamdigital.interfaces;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by HP on 30-12-2016.
 */

public interface MainActivityAdapterListeners {
    public void browserButtonClicked(int position);

    public void contentButtonClicked(int position, View holder);

    public void levelButtonClicked(int position);

    public void downloadClick(int position, View holder);

    public void downloadComplete(int position);

    public void onContentDelete(int position);
}
