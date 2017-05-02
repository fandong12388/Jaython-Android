package com.jaython.cc.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaython.cc.R;
import com.jaython.cc.bean.ActionCompose;
import com.jaython.cc.data.manager.LoginManager;
import com.jaython.cc.data.model.ComposeModel;
import com.jaython.cc.ui.view.JToast;
import com.jaython.cc.ui.view.LikeAnimation;
import com.tiny.loader.TinyImageLoader;
import com.tiny.loader.internal.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2017/1/14
 * description:
 *
 * @author fandong
 */
public class ActionComposeAdapter extends BaseAdapter {

    private Context mContext;
    private List<ActionCompose> mCompose;
    private LayoutInflater mLayoutInflater;
    private ComposeModel mModel;
    private boolean mIsCollected;

    public ActionComposeAdapter(Context context, List<ActionCompose> composes, ComposeModel model) {
        this.mContext = context;
        this.mModel = model;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mCompose = new ArrayList<>();
        this.mCompose.addAll(composes);
    }

    public void setCollected(boolean collected) {
        this.mIsCollected = collected;
    }

    public void addCompose(List<ActionCompose> compose) {
        if (this.mCompose == null) {
            this.mCompose = new ArrayList<>();
        }
        this.mCompose.addAll(compose);
    }

    public void clearCompose() {
        if (this.mCompose != null) {
            this.mCompose.clear();
        }
    }

    @Override
    public int getCount() {
        return mCompose.size();
    }

    @Override
    public Object getItem(int position) {
        return mCompose.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = mLayoutInflater.inflate(R.layout.vw_compose_list_item, null);
        }
        ActionCompose compose = mCompose.get(position);
        ViewHolder holder = new ViewHolder(convertView);
        TinyImageLoader.create(compose.getImageUrl1())
                .setImageScaleType(ImageScaleType.EXACTLY)
                .setDisplayType(TinyImageLoader.DISPLAY_BLUR)
                .setBlurDepth(6)
                .into(holder.iv);
        //是否收藏
        if (compose.isCollected() || mIsCollected) {
            holder.like.setImageResource(R.drawable.like_press);
            holder.like.setTag(true);
        } else {
            holder.like.setImageResource(R.drawable.like_normal);
            holder.like.setTag(false);
        }

        holder.like.setOnClickListener(v -> {
            if (!(Boolean) v.getTag()) {
                if (LoginManager.getInstance().isLoginValidate()) {
                    holder.like.setImageResource(R.drawable.like_press);
                    v.setTag(true);
                    String text = holder.num.getText().toString();
                    Integer num = Integer.valueOf(text) + 1;
                    holder.num.setText(num + "");
                    //动画
                    v.clearAnimation();
                    v.startAnimation(new LikeAnimation(2.0f, 0.8f, 1.0f));
                    //发送请求
                    mModel.collectCompose(LoginManager.getInstance().getUid(), compose.getId());
                } else {
                    JToast.show(R.string.tip_login, mContext);
                }
            }
        });
        holder.num.setText(compose.getCollects() + "");
        holder.title.setText(compose.getTitle());
        return convertView;
    }

    class ViewHolder {

        @InjectView(R.id.compose_list_item_iv)
        ImageView iv;
        @InjectView(R.id.compose_list_item_like)
        ImageView like;
        @InjectView(R.id.compose_list_item_num)
        TextView num;
        @InjectView(R.id.compose_list_item_title)
        TextView title;


        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
