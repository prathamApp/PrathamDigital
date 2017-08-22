package com.pratham.prathamdigital.adapters;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.interfaces.MainActivityAdapterListeners;
import com.pratham.prathamdigital.models.Modal_ContentDetail;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HP on 01-08-2017.
 */

public class RV_LibraryContentAdapter extends RecyclerView.Adapter<RV_LibraryContentAdapter.ViewHolder> {

    private Context context;
    private MainActivityAdapterListeners browseAdapter_clicks;
    private int selectedIndex;
    private ArrayList<Modal_ContentDetail> downloadContents;

    public RV_LibraryContentAdapter(Context context, MainActivityAdapterListeners browseAdapter_clicks,
                                    ArrayList<Modal_ContentDetail> downloadContents) {
        this.context = context;
        this.browseAdapter_clicks = browseAdapter_clicks;
        this.downloadContents = downloadContents;
        selectedIndex = -1;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_age_filter, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        try {
            if (downloadContents.size() > 0) {
                holder.c_age.setText(downloadContents.get(position).getNodetitle());
                String fileName = downloadContents.get(position).getNodeserverimage()
                        .substring(downloadContents.get(position).getNodeserverimage().lastIndexOf('/') + 1);
                //path to /data/data/yourapp/app_data/dirName
                ContextWrapper cw = new ContextWrapper(context);
                File directory = cw.getDir("PrathamImages", Context.MODE_PRIVATE);
                File filepath = new File(directory, fileName);
                Log.d("adapter_filename:::", fileName);
                Log.d("adapter_filepath:::", filepath.toString());
                holder.child_avatar.setImageDrawable(Drawable.createFromPath(filepath.toString()));
            }
            holder.card_age.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    browseAdapter_clicks.browserButtonClicked(holder.getAdapterPosition());
                }
            });
            if (selectedIndex != -1 && selectedIndex == position) {
                holder.card_age.setBackgroundColor(context.getResources().getColor(R.color.ghost_white));
            } else {
                holder.card_age.setBackground(context.getResources().getDrawable(R.drawable.pink_blue_gradient));
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setSelectedIndex(int ind) {
        selectedIndex = ind;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return downloadContents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.child_avatar)
        ImageView child_avatar;
        @BindView(R.id.card_age)
        RelativeLayout card_age;
        @BindView(R.id.c_age)
        TextView c_age;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
