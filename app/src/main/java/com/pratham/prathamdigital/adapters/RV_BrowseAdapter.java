package com.pratham.prathamdigital.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.interfaces.MainActivityAdapterListeners;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HP on 01-08-2017.
 */

public class RV_BrowseAdapter extends RecyclerView.Adapter<RV_BrowseAdapter.ViewHolder> {

    private Context context;
    private MainActivityAdapterListeners browseAdapter_clicks;
    private String[] name;
    private int selectedIndex;

    public RV_BrowseAdapter(Context context, MainActivityAdapterListeners browseAdapter_clicks, String[] name) {
        this.context = context;
        this.browseAdapter_clicks = browseAdapter_clicks;
        this.name = name;
        selectedIndex = -1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_browse_content, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (selectedIndex != -1 && selectedIndex == position)
            holder.bc_card.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        else
            holder.bc_card.setCardBackgroundColor(context.getResources().getColor(R.color.ghost_white));
        holder.bc_name.setText(name[position]);
        holder.bc_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browseAdapter_clicks.browserButtonClicked(position);
            }
        });
    }

    public void setSelectedIndex(int ind) {
        selectedIndex = ind;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return name.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.bc_card)
        CardView bc_card;
        @BindView(R.id.bc_name)
        TextView bc_name;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
