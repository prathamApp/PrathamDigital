package com.pratham.prathamdigital.adapters;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.progress_indicators.ProgressLayout;
import com.pratham.prathamdigital.custom.progress_indicators.ProgressLayoutListener;
import com.pratham.prathamdigital.interfaces.MainActivityAdapterListeners;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HP on 01-08-2017.
 */

public class RV_RecommendAdapter extends RecyclerView.Adapter<RV_RecommendAdapter.ViewHolder> {

    private Context context;
    private MainActivityAdapterListeners browseAdapter_clicks;
    private ArrayList<Modal_ContentDetail> sub_content;
    private int selectedIndex;
    private int progress = 0;
    private static final int SECOND_MS = 1000;
    private boolean isPlaying = false;
    private Handler mHandler = new Handler();
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            progress += 1;
            mHandler.postDelayed(mRunnable, SECOND_MS);
        }
    };

    public RV_RecommendAdapter(Context context, MainActivityAdapterListeners browseAdapter_clicks,
                               ArrayList<Modal_ContentDetail> sub_content) {
        this.context = context;
        this.browseAdapter_clicks = browseAdapter_clicks;
        this.sub_content = sub_content;
        selectedIndex = -1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.recom_name.setText(sub_content.get(position).getNodetitle());
        Picasso.with(context).load(sub_content.get(position).getNodeserverimage()).into(holder.recommend_content_img);
        holder.recom_progressLayout.setMaxProgress(100);
        if (selectedIndex != -1 && selectedIndex == position) {
            holder.recom_img_download.setVisibility(View.INVISIBLE);
            holder.recom_progressbar.setVisibility(View.VISIBLE);
            holder.recom_progressLayout.setCurrentProgress(progress);
            if (isPlaying) holder.recom_progressLayout.start();
        } else {
            holder.recom_progressLayout.cancel();
        }
        holder.recom_progressLayout.setProgressLayoutListener(new ProgressLayoutListener() {
            @Override
            public void onProgressCompleted() {
                holder.recom_progressLayout.stop();
                holder.recom_progressbar.setVisibility(View.INVISIBLE);
                progress = 0;
            }

            @Override
            public void onProgressChanged(int seconds) {
                Log.d("progress::", "is changed");
            }
        });
        holder.recom_img_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browseAdapter_clicks.downloadClick(position, holder);
            }
        });
        holder.card_recom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browseAdapter_clicks.contentButtonClicked(position);
            }
        });
    }

    public void setSelectedIndex(int position, ViewHolder holder) {
//        selectedIndex = ind;
        if (selectedIndex != position) {
            selectedIndex = position;
//            mHandler.removeCallbacks(mRunnable);
            progress = 0;
        }
        if (!holder.recom_progressLayout.isPlaying()) {
            isPlaying = true;
            holder.recom_progressLayout.start();
//            mHandler.postDelayed(mRunnable, 0);
        } else {
            isPlaying = false;
            holder.recom_progressLayout.stop();
//            mHandler.removeCallbacks(mRunnable);
        }
        notifyDataSetChanged();
    }

    public void setProgress(int pro) {
        progress = pro;
        Log.d("progress::", "" + progress + "::::::" + pro);
    }

    public void updateData(ArrayList<Modal_ContentDetail> arrayList_content) {
        this.sub_content=arrayList_content;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return sub_content.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.recom_img_download)
        ImageView recom_img_download;
        @BindView(R.id.recom_name)
        TextView recom_name;
        @BindView(R.id.recom_progressbar)
        ProgressBar recom_progressbar;
        @BindView(R.id.recom_progressLayout)
        ProgressLayout recom_progressLayout;
        @BindView(R.id.recommend_content_img)
        ImageView recommend_content_img;
        @BindView(R.id.card_recom)
        RelativeLayout card_recom;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
