package com.jaython.cc.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaython.cc.R;
import com.jaython.cc.bean.ActionCompose;
import com.jaython.cc.bean.ComposeAction;
import com.jaython.cc.data.manager.LoginManager;
import com.jaython.cc.data.model.ComposeModel;
import com.jaython.cc.ui.ActionDetailActivity;
import com.jaython.cc.ui.view.JToast;
import com.jaython.cc.ui.view.LikeAnimation;
import com.jaython.cc.ui.widget.SquareImageView;
import com.jaython.cc.utils.ValidateUtil;
import com.jaython.cc.utils.helper.ResHelper;
import com.tiny.loader.TinyImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2017/1/15
 * description:
 *
 * @author fandong
 */
public class ComposeDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_HEAD = 1;
    public static final int TYPE_ITEM = 2;

    private ActionCompose mCompose;
    private Context mContext;
    private ComposeModel mModel;


    public ComposeDetailAdapter(Context context, ActionCompose compose, ComposeModel model) {
        this.mCompose = compose;
        this.mModel = model;
        this.mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (0 == position) {
            return TYPE_HEAD;
        }
        return TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (TYPE_HEAD == viewType) {
            return new HeadViewHolder(inflater.inflate(R.layout.vw_compose_recycler_item1, parent, false));
        }
        return new ItemViewHolder(inflater.inflate(R.layout.vw_action_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (TYPE_HEAD == getItemViewType(position)) {
            HeadViewHolder headViewHolder = (HeadViewHolder) holder;
            //1.标题
            headViewHolder.mTitle.setText(mCompose.getTitle());
            //2.组数
            String num = ResHelper.getString(R.string.compose_item_num);
            headViewHolder.mNum.setText(String.format(num, mCompose.getNum()));
            //3.休息时间
            String second = ResHelper.getString(R.string.compose_item_second);
            headViewHolder.mSecond.setText(String.format(second, mCompose.getRestSec()));

            TinyImageLoader.create(mCompose.getImageUrl2())
                    .setBlurDepth(12)
                    .setDisplayType(TinyImageLoader.DISPLAY_BLUR)
                    .into(headViewHolder.mIvBg);
            //4.喜欢
            //是否收藏
            if (mCompose.isCollected()) {
                headViewHolder.mLike.setImageResource(R.drawable.like_press);
                headViewHolder.mLike.setTag(true);

            } else {
                headViewHolder.mLike.setImageResource(R.drawable.like_normal);
                headViewHolder.mLike.setTag(false);
            }

            headViewHolder.mLikeTv.setText(mCompose.getCollects() + "");

            headViewHolder.mLike.setOnClickListener(v -> {
                if (!(Boolean) v.getTag()) {
                    if (LoginManager.getInstance().isLoginValidate()) {
                        headViewHolder.mLike.setImageResource(R.drawable.like_press);
                        v.setTag(true);
                        String text = headViewHolder.mLikeTv.getText().toString();
                        Integer likeNum = Integer.valueOf(text) + 1;
                        headViewHolder.mLikeTv.setText(likeNum + "");
                        //动画
                        v.clearAnimation();
                        v.startAnimation(new LikeAnimation(2.0f, 0.8f, 1.0f));
                        //发送请求
                        mModel.collectCompose(LoginManager.getInstance().getUid(), mCompose.getId());
                    } else {
                        JToast.show(R.string.tip_login, mContext);
                    }
                }
            });
        } else {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            String format = ResHelper.getString(R.string.compose_item_group);
            ComposeAction action = mCompose.getActions().get(position - 1);
            if (action != null) {
                TinyImageLoader.create(action.getAction().getIcon())
                        .into(itemViewHolder.mImageView);
                itemViewHolder.mDesc.setText(String.format(format, action.getNum(), action.getSum()));
                itemViewHolder.mTitle.setText(action.getAction().getTitle());
            }
            itemViewHolder.itemView.setOnClickListener(v ->
                    ActionDetailActivity.launch(mContext, action.getAction()));
        }
    }

    @Override
    public int getItemCount() {
        if (ValidateUtil.isValidate(mCompose.getActions())) {
            return mCompose.getActions().size() + 1;
        }
        return 0;
    }

    public static class HeadViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.compose_item_title)
        TextView mTitle;
        @InjectView(R.id.compose_item_num)
        TextView mNum;
        @InjectView(R.id.compose_item_second)
        TextView mSecond;
        @InjectView(R.id.compose_list_item_like)
        ImageView mLike;
        @InjectView(R.id.compose_list_item_num)
        TextView mLikeTv;
        @InjectView(R.id.compose_bg_iv)
        ImageView mIvBg;

        public HeadViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.action_item_iv)
        SquareImageView mImageView;
        @InjectView(R.id.action_item_title)
        TextView mTitle;
        @InjectView(R.id.action_item_desc)
        TextView mDesc;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
