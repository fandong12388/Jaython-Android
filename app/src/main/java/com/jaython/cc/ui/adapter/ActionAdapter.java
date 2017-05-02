package com.jaython.cc.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaython.cc.R;
import com.jaython.cc.bean.Action;
import com.jaython.cc.ui.ActionDetailActivity;
import com.jaython.cc.utils.PixelUtil;
import com.tiny.loader.TinyImageLoader;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2017/1/13
 * description:
 *
 * @author fandong
 */
public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private List<Action> mList;

    public ActionAdapter(Context context, List<Action> list) {
        this.mContext = context;
        this.mList = list;
        this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.vw_action_item, null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Action action = mList.get(position);
        TinyImageLoader.create(action.getIcon())
                .setDefaultRes(R.drawable.logo_loading)
                .into(holder.iv);
        holder.title.setText(action.getTitle());
        holder.description.setText(action.getDescription());
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                (int) PixelUtil.dp2px(108.f)
        );
        holder.itemView.setLayoutParams(params);
        holder.itemView.setOnClickListener(v -> ActionDetailActivity.launch(mContext, action));
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.action_item_iv)
        ImageView iv;
        @InjectView(R.id.action_item_title)
        TextView title;
        @InjectView(R.id.action_item_desc)
        TextView description;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
