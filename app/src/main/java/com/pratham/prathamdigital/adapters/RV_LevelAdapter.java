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

public class RV_LevelAdapter extends RecyclerView.Adapter<RV_LevelAdapter.ViewHolder> {

    private Context context;
    private MainActivityAdapterListeners browseAdapter_clicks;
    private String[] levels;
    private int selectedIndex;

    public RV_LevelAdapter(Context context, MainActivityAdapterListeners browseAdapter_clicks, String[] levels) {
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
        if (selectedIndex != -1 && selectedIndex == position)
            holder.l_card.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        else
            holder.l_card.setCardBackgroundColor(context.getResources().getColor(R.color.ghost_white));
        holder.l_name.setText(levels[position]);
//        holder.l_card.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                browseAdapter_clicks.browserButtonClicked(position);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return levels.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.l_card)
        CardView l_card;
        @BindView(R.id.l_name)
        TextView l_name;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
