package com.pratham.prathamdigital.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.interfaces.MainActivityAdapterListeners;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HP on 01-08-2017.
 */

public class RV_ContentAdapter extends RecyclerView.Adapter<RV_ContentAdapter.ViewHolder> {

    private Context context;
    private MainActivityAdapterListeners browseAdapter_clicks;
    private String[] sub_content;
    private int selectedIndex;

    public RV_ContentAdapter(Context context, MainActivityAdapterListeners browseAdapter_clicks, String[] sub_content) {
        this.context = context;
        this.browseAdapter_clicks = browseAdapter_clicks;
        this.sub_content = sub_content;
        selectedIndex = -1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_content, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.c_name.setText(sub_content[position]);
        holder.img_folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browseAdapter_clicks.contentButtonClicked(position);
            }
        });
    }

//    public void setSelectedIndex(int ind) {
//        selectedIndex = ind;
//        notifyDataSetChanged();
//    }

    @Override
    public int getItemCount() {
        return sub_content.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_folder)
        ImageView img_folder;
        @BindView(R.id.c_name)
        TextView c_name;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
