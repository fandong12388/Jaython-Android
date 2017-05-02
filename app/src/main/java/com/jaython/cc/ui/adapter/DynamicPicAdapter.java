package com.jaython.cc.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jaython.cc.R;
import com.jaython.cc.ui.widget.SquareImageView;
import com.jaython.cc.utils.PixelUtil;
import com.jaython.cc.utils.ValidateUtil;
import com.jaython.cc.utils.helper.ResHelper;
import com.tiny.loader.TinyImageLoader;

import java.util.List;

/**
 * time: 17/2/2
 * description:
 *
 * @author fandong
 */
public class DynamicPicAdapter extends RecyclerView.Adapter<DynamicPicAdapter.ViewHolder> {
    private Context mContext;
    private List<String> mPictures;
    //整体的宽度
    private int mSize;
    private int mDivider;

    private OnItemClickListener mOnItemClickListener;

    public DynamicPicAdapter(Context context, List<String> pics) {
        this.mContext = context;
        this.mPictures = pics;
        this.mSize = (int) (ResHelper.getScreenWidth() - PixelUtil.dp2px(60.5f));
        this.mDivider = (int) PixelUtil.dp2px(4.f);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SquareImageView iv = new SquareImageView(mContext);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new ViewHolder(iv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SquareImageView iv = (SquareImageView) holder.itemView;
        iv.setTag(position);
        //1.如果只有一张就显示70%的宽度
        if (getItemCount() == 1) {
            int size = (int) (0.7 * mSize);
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(size, size);
            iv.setLayoutParams(params);

        } else if (getItemCount() == 2) {
            int size = (int) (0.86 * mSize) / 2;
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(size, size);
            iv.setLayoutParams(params);
        } else {
            int size = (mSize - 4 * mDivider) / 3;
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(size, size);
            iv.setLayoutParams(params);
        }
        String url = mPictures.get(position);
        if (!url.startsWith("http")) {
            url = "file://" + url;
        }
        TinyImageLoader.create(url)
                .setDefaultRes(R.drawable.logo_loading)
                .into(iv);
        iv.setOnClickListener(v -> {
            if (null != mOnItemClickListener) {
                mOnItemClickListener.onItemClick(v);
            }
        });

    }

    @Override
    public int getItemCount() {
        return ValidateUtil.isValidate(mPictures) ? mPictures.size() : 0;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
