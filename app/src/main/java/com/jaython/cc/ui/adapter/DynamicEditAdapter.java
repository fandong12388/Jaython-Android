package com.jaython.cc.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jaython.cc.R;
import com.jaython.cc.utils.PixelUtil;
import com.jaython.cc.utils.ValidateUtil;
import com.jaython.cc.utils.helper.ResHelper;
import com.tiny.loader.TinyImageLoader;
import com.tiny.loader.internal.core.assist.ImageScaleType;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time: 17/1/29
 * description:
 *
 * @author fandong
 */
public class DynamicEditAdapter extends RecyclerView.Adapter<DynamicEditAdapter.ViewHolder> {

    private ArrayList<String> mPaths;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    private int mItemSize;

    private boolean mIsDeleteStatus;

    private OnItemClickListener mOnItemClickListener;

    public DynamicEditAdapter(Context context) {
        this.mPaths = new ArrayList<>();
        this.mContext = context;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mItemSize = (int) ((ResHelper.getScreenWidth() - 8 * PixelUtil.dp2px(4.f)) / 4);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void addPath(String path) {
        if (!mPaths.contains(path)) {
            if (mPaths.size() >= 9) {
                mPaths.remove(0);
            }
            mPaths.add(path);
        } else {
            if (mPaths.remove(path)) {
                mPaths.add(path);
            }
        }
    }

    public void removeItem(int pos) {
        mPaths.remove(pos);
        notifyItemRemoved(pos);
    }

    public boolean isDeleteStatus() {
        return mIsDeleteStatus;
    }

    public void setDeleteStatus(boolean status) {
        this.mIsDeleteStatus = status;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.vw_dynamic_image_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //if (holder.itemView.getLayoutParams() == null) {
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(mItemSize, mItemSize);
        holder.itemView.setLayoutParams(params);
        //}
        //1.如果是最后一个
        if (position == getItemCount() - 1) {
            holder.deleteLayout.setVisibility(View.GONE);
            if (mIsDeleteStatus) {
                holder.iv.setVisibility(View.GONE);
            } else {
                holder.iv.setVisibility(View.VISIBLE);
                holder.iv.setImageResource(R.drawable.icon_dynamic_add);
            }
        } else {
            holder.iv.setVisibility(View.VISIBLE);
            holder.delete.setTag(position);
            if (mIsDeleteStatus) {
                holder.deleteLayout.setVisibility(View.VISIBLE);
                holder.delete.setOnClickListener(v -> {
                    if (null != mOnItemClickListener) {
                        mOnItemClickListener.onItemDeleteClick(v);
                    }
                });
            } else {
                holder.deleteLayout.setVisibility(View.GONE);
            }
            String contentPath = mPaths.get(position);
            if (ValidateUtil.isValidate(contentPath)) {
                TinyImageLoader.create("file://" + mPaths.get(position))
                        .setImageScaleType(ImageScaleType.NONE)
                        .into(holder.iv);
            }
        }
        holder.itemView.setOnClickListener(view -> {
            if (null != mOnItemClickListener) {
                mOnItemClickListener.onItemClick(view, position);
            }
        });
        holder.itemView.setOnLongClickListener(view -> {
            if (null != mOnItemClickListener) {
                mOnItemClickListener.onItemLongClick(view, position);
            }
            return true;
        });
    }


    @Override
    public int getItemCount() {
        return mPaths.size() + 1;
    }

    public ArrayList<String> getPaths() {
        return mPaths;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);

        void onItemDeleteClick(View view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.dynamic_image_item)
        ImageView iv;
        @InjectView(R.id.dynamic_image_delete)
        ImageView delete;
        @InjectView(R.id.dynamic_image_delete_layout)
        FrameLayout deleteLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
