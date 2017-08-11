package com.pratham.prathamdigital.adapters;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.interfaces.MainActivityAdapterListeners;
import com.pratham.prathamdigital.models.Modal_Level;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HP on 01-08-2017.
 */

public class RV_LevelAdapter extends RecyclerView.Adapter<RV_LevelAdapter.ViewHolder> {

    private Context context;
    private MainActivityAdapterListeners browseAdapter_clicks;
    private ArrayList<Modal_Level> levels;
    private int selectedIndex;

    public RV_LevelAdapter(Context context, MainActivityAdapterListeners browseAdapter_clicks, ArrayList<Modal_Level> levels) {
        this.context = context;
        this.browseAdapter_clicks = browseAdapter_clicks;
        this.levels = levels;
        selectedIndex = -1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_level, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.l_name.setText(levels.get(position).getName());
        holder.l_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browseAdapter_clicks.levelButtonClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return levels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.l_card)
        LinearLayout l_card;
        @BindView(R.id.l_name)
        TextView l_name;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
