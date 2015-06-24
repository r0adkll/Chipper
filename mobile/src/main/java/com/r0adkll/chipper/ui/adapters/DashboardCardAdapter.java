package com.r0adkll.chipper.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.r0adkll.chipper.R;
import com.r0adkll.chipper.ui.screens.dashboard.model.DashboardCard;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by r0adkll on 12/16/14.
 */
public class DashboardCardAdapter extends RecyclerArrayAdapter<DashboardCard, DashboardCardAdapter.CardViewHolder> {

    @Inject
    public DashboardCardAdapter(){
        super();
    }

    @Override
    public boolean onQuery(DashboardCard item, String query) {
        return true;
    }

    @Override
    public void onSort(List<DashboardCard> items) {}

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_dashboard_item, parent, false);

        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        DashboardCard card = getItem(position);

        CharSequence title = card.getTitle();
        if(title != null){
            holder.title.setVisibility(View.VISIBLE);
            holder.title.setText(title);
        }else{
            holder.title.setVisibility(View.GONE);
        }

        View oldContent = holder.content.getChildCount() > 0 ? holder.content.getChildAt(0) : null;
        if(oldContent != null && oldContent.getId() != card.getId()) oldContent = null;

        View newContent = card.getContentView(oldContent);
        newContent.setId(card.getId());

        holder.content.removeAllViews();
        holder.content.addView(newContent);
    }

    class CardViewHolder extends RecyclerView.ViewHolder{

        @InjectView(R.id.title)         TextView title;
        @InjectView(R.id.content)       FrameLayout content;

        public CardViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

}
