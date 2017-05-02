package com.jaython.cc.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaython.cc.R;
import com.jaython.cc.bean.ActionGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time: 2017/1/13
 * description:
 *
 * @author fandong
 */
public class HomeTitleAdapter extends RecyclerView.Adapter<HomeTitleAdapter.ViewHolder> {


    private List<String> mNames;
    private Context mContext;

    private int mSelected;
    private OnItemClickListener mOnItemClickListener;

    public HomeTitleAdapter(Context context, List<ActionGroup> groups) {
        this.mContext = context;
        this.mNames = new ArrayList<>();
        for (ActionGroup group : groups) {
            if (!this.mNames.contains(group.getTitle())) {
                this.mNames.add(group.getTitle());
            }
        }
    }

    public void setSelected(int position) {
        this.mSelected = position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.vw_home_title_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        if (mSelected == position) {
            TextPaint paint = holder.mTitle.getPaint();
            paint.setFakeBoldText(true);
            holder.mIndicator.setVisibility(View.VISIBLE);
        } else {
            TextPaint paint = holder.mTitle.getPaint();
            paint.setFakeBoldText(false);
            holder.mIndicator.setVisibility(View.INVISIBLE);
        }
        holder.mTitle.setText(mNames.get(position));
        holder.itemView.setOnClickListener(v -> {
            if (null != mOnItemClickListener) {
                mOnItemClickListener.onItemClick(v, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.home_title_indicator)
        TextView mIndicator;
        @InjectView(R.id.home_title_text)
        TextView mTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
