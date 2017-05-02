package com.jaython.cc.ui.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaython.cc.R;
import com.jaython.cc.bean.NewsItem;
import com.jaython.cc.ui.view.FooterView;
import com.jaython.cc.ui.view.ImageGridLayout;
import com.jaython.cc.utils.PixelUtil;
import com.jaython.cc.utils.ValidateUtil;
import com.tiny.loader.TinyImageLoader;
import com.tiny.loader.internal.core.assist.ImageScaleType;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * time:2016/11/25
 * description:
 *
 * @author sunjianfei
 */
public class NewsListAdapter extends AbsRecycleAdapter<NewsItem, RecyclerView.ViewHolder> {

    public static final int NEWS_ITEM_TYPE_FOOTER = 2;

    private OnItemClickListener mOnItemClickListener;

    private boolean mIsCompleted;

    public boolean isCompleted() {
        return mIsCompleted;
    }

    public void setCompleted(boolean completed) {
        this.mIsCompleted = completed;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItemCount() == mData.size() + 1
                && position == getItemCount() - 1) {
            return NEWS_ITEM_TYPE_FOOTER;
        }
        return mData.get(position).getItemType();
    }

    @Override
    public int getItemCount() {
        if (ValidateUtil.isValidate(mData)) {
            if (mData.size() >= 10) {
                if (mData.size() % 10 != 0) {
                    return mData.size();
                } else {
                    return mData.size() + 1;
                }
            } else {
                return mData.size();
            }
        } else {
            return 0;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        Context context = parent.getContext();
        if (viewType == NewsItem.NEWS_ITEM_TYPE_1) {
            view = LayoutInflater.from(context).inflate(R.layout.vw_news_item_type_1, parent, false);
            return new NewsItemViewHolder(view);
        } else if (viewType == NewsItem.NEWS_ITEM_TYPE_2) {
            view = LayoutInflater.from(context).inflate(R.layout.vw_news_item_type_2, parent, false);
            return new NewsItemViewHolder(view);
        }
        return new FooterViewHolder(new FooterView(context));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == NEWS_ITEM_TYPE_FOOTER) {
            FooterView footerView = (FooterView) holder.itemView;
            if (mIsCompleted) {
                footerView.hideView();
            } else {
                footerView.showLoadMoreTv();
                footerView.setOnLoadMoreClickListener(view -> {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onLastItemClickListener(footerView);
                    }
                });
            }

        } else {
            Context context = holder.itemView.getContext();
            NewsItemViewHolder viewHolder = (NewsItemViewHolder) holder;
            int type = getItemViewType(position);
            NewsItem mItem = mData.get(position);

            viewHolder.mNewsTitleTv.setText(mItem.getTitle());
            viewHolder.mNewsDataTv.setText(context.getString(R.string.news_total_number
                    , mItem.getVisit()
                    , mItem.getComment()
                    , mItem.getCollect()));
            viewHolder.mTopicNameTv.setText(mItem.getCategory());
            if (type == NewsItem.NEWS_ITEM_TYPE_1) {
                viewHolder.mNewsContentTv.setText(mItem.getAbstracts());
                int size = (int) PixelUtil.dp2px(100);
                TinyImageLoader.create(mItem.getImageUrl1())
                        .setQiniu(size, size)
                        .setImageScaleType(ImageScaleType.EXACTLY)
                        .setDisplayType(TinyImageLoader.DISPLAY_DEFAULT)
                        .setDefaultDrawable(new ColorDrawable(0xffe0dedc))
                        .into(viewHolder.mImageView);
            } else {
                viewHolder.mImageGridView.setData(mItem.getAllImage());
            }
            holder.itemView.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    if (getItemViewType(position) != NEWS_ITEM_TYPE_FOOTER) {
                        mOnItemClickListener.onItemClickListener(v, mItem.getId());
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClickListener(View view, int id);

        void onLastItemClickListener(FooterView view);
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    class NewsItemViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.news_title_tv)
        TextView mNewsTitleTv;
        @Optional
        @InjectView(R.id.news_content_tv)
        TextView mNewsContentTv;
        @InjectView(R.id.news_data_tv)
        TextView mNewsDataTv;
        @InjectView(R.id.topic_name_tv)
        TextView mTopicNameTv;
        @Optional
        @InjectView(R.id.news_image)
        ImageView mImageView;
        @Optional
        @InjectView(R.id.news_grid_view)
        ImageGridLayout mImageGridView;

        public NewsItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
