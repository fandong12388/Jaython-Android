package com.jaython.cc.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaython.cc.R;
import com.jaython.cc.bean.NewsComment;
import com.jaython.cc.bean.UserProfile;
import com.jaython.cc.utils.PixelUtil;
import com.jaython.cc.utils.ValidateUtil;
import com.jaython.cc.utils.helper.ViewUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2016/11/28
 * description:
 *
 * @author sunjianfei
 */
public class NewsCommentAdapter extends AbsListAdapter<NewsComment> {
    private static final int TYPE_TITLE = 0x000;
    private static final int TYPE_DEFAULT = 0x001;

    @Override
    public int getCount() {
        return ValidateUtil.isValidate(mData) ? mData.size() + 1 : 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_TITLE : TYPE_DEFAULT;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        CommentViewHolder holder = null;
        int type = getItemViewType(position);
        if (null == convertView) {
            if (type == TYPE_TITLE) {
                convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.vw_news_comment_title, viewGroup, false);
            } else {
                convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.vw_news_comment, viewGroup, false);
                holder = new CommentViewHolder(convertView);
                convertView.setTag(holder);
            }
        } else {
            if (type == TYPE_DEFAULT) {
                holder = (CommentViewHolder) convertView.getTag();
            }
        }

        if (type == TYPE_DEFAULT) {
            NewsComment comment = mData.get(position - 1);
            UserProfile profile = comment.getUserProfile();
            int size = (int) PixelUtil.dp2px(60);
            //显示头像
            ViewUtils.showCirCleAvatar(holder.mAvatarIv, profile.getHeadimgurl(), size);
            holder.mNicknameTv.setText(profile.getNickname());
            holder.mCommentContentTv.setText(comment.getContent());
        }
        return convertView;
    }

    class CommentViewHolder {
        @InjectView(R.id.avatar_iv)
        ImageView mAvatarIv;
        @InjectView(R.id.nick_name_tv)
        TextView mNicknameTv;
        @InjectView(R.id.comment_content_tv)
        TextView mCommentContentTv;


        public CommentViewHolder(View itemView) {
            ButterKnife.inject(this, itemView);
        }
    }
}
