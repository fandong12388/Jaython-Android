package com.jaython.cc.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaython.cc.R;
import com.jaython.cc.bean.DynamicComment;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2017/2/11
 * description:
 *
 * @author fandong
 */
public class DynamicCommentAdapter extends RecyclerView.Adapter<DynamicCommentAdapter.ViewHolder> {

    private List<DynamicComment> comments;
    private Context mContext;

    public DynamicCommentAdapter(Context context, List<DynamicComment> comments) {
        this.mContext = context;
        this.comments = comments;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new ViewHolder(inflater.inflate(R.layout.vw_dynamic_comment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DynamicComment comment = comments.get(position);
        String username = comment.getUser().getNickname();
        if (TextUtils.isEmpty(username)) {
            username = "匿名用户";
        }
        String format = "<font color='#4798dc'>%1$s</font> ：<font color='#121212'>%2$s</font>";
        String content = String.format(format
                , username
                , comment.getContent());
        holder.mTextView.setText(Html.fromHtml(content));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.dynamic_comment_tv)
        TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
