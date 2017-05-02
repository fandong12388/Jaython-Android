package com.jaython.cc.ui.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaython.cc.R;
import com.jaython.cc.bean.Dynamic;
import com.jaython.cc.bean.DynamicComment;
import com.jaython.cc.bean.UserProfile;
import com.jaython.cc.data.constants.EventConstant;
import com.jaython.cc.data.manager.LoginManager;
import com.jaython.cc.data.manager.RxBusManager;
import com.jaython.cc.data.model.DynamicModel;
import com.jaython.cc.ui.DynamicDetailActivity;
import com.jaython.cc.ui.view.FooterView;
import com.jaython.cc.ui.view.JToast;
import com.jaython.cc.ui.view.LikeAnimation;
import com.jaython.cc.ui.view.divider.GridRecyclerDecoration;
import com.jaython.cc.utils.NumberUtil;
import com.jaython.cc.utils.PixelUtil;
import com.jaython.cc.utils.TimeUtil;
import com.jaython.cc.utils.ValidateUtil;
import com.jaython.cc.utils.helper.ResHelper;
import com.tiny.loader.TinyImageLoader;
import com.tiny.loader.internal.core.assist.ImageScaleType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time: 17/2/1
 * description:
 *
 * @author fandong
 */
public class DynamicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_CONTENT = 1;
    private static final int TYPE_FOOTER = 2;

    private Context mContext;
    private ArrayList<Dynamic> mList;
    private LayoutInflater mInflater;
    private OnDynamicClickListener mOnDynamicClickListener;

    private int mPicItemSize;

    private int mPicWidth;

    private boolean mIsCompleted;

    private DynamicModel mViewModel;

    public DynamicAdapter(Context context, DynamicModel viewModel) {
        this.mViewModel = viewModel;
        this.mContext = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mList = new ArrayList<>();
        this.mPicItemSize = (int) ((ResHelper.getScreenWidth() - PixelUtil.dp2px(68.5f)) / 3.f);
        this.mPicWidth = (int) (ResHelper.getScreenWidth() - PixelUtil.dp2px(60.5f));
    }

    public int addDynamicComment(DynamicComment dynamicComment) {
        if (ValidateUtil.isValidate(mList)) {
            for (int i = 0; i < mList.size(); i++) {
                Dynamic dynamic = mList.get(i);
                if (dynamic.getId() == dynamicComment.getDynamicId().intValue()) {
                    dynamic.addDynamicComment(dynamicComment);
                    int comment = dynamic.getComment();
                    dynamic.setComment(comment + 1);
                    return i;
                }

            }
        }
        return -1;
    }

    public boolean isCompleted() {
        return mIsCompleted;
    }

    public void setCompleted(boolean completed) {
        this.mIsCompleted = completed;
    }

    public void addDynamics(ArrayList<Dynamic> dynamics) {
        for (Dynamic dynamic : dynamics) {
            if (!this.mList.contains(dynamic)) {
                this.mList.add(dynamic);
            }
        }
    }

    public void addDynamic(int index, Dynamic dynamics) {
        this.mList.add(index, dynamics);
    }

    public void refreshDynamics(ArrayList<Dynamic> dynamics) {
        this.mList = dynamics;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        }
        return TYPE_CONTENT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (TYPE_FOOTER == viewType) {
            return new FooterViewHolder(new FooterView(mContext));
        }
        return new ViewHolder(mInflater.inflate(R.layout.vw_dynamic_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) viewHolder;
            Dynamic dynamic = mList.get(position);
            UserProfile userProfile = dynamic.getUserProfile();
            if (null != userProfile) {
                //1.头像
                TinyImageLoader.create(userProfile.getHeadimgurl())
                        .setImageScaleType(ImageScaleType.EXACTLY)
                        .setDefaultRes(R.drawable.icon_user_avatar_default_92)
                        .setDisplayType(TinyImageLoader.DISPLAY_CIRCLE)
                        .into(holder.userAvatar);
                //2.处理用户名
                holder.userName.setText(TextUtils.isEmpty(userProfile.getNickname()) ? "匿名用户" : userProfile.getNickname());
            } else {
                holder.userAvatar.setImageResource(R.drawable.icon_user_avatar_default_92);
                holder.userName.setText("匿名用户");
            }
            //3.处理内容
            holder.content.setText(TextUtils.isEmpty(dynamic.getContent()) ? "" : dynamic.getContent());
            //4.处理图片显示
            if (ValidateUtil.isValidate(dynamic.getImages())) {
                holder.mPictureRecycler.setVisibility(View.VISIBLE);
                initPicRecyclerView(holder.mPictureRecycler, dynamic.getImages(), dynamic.getContent());
            } else {
                holder.mPictureRecycler.setVisibility(View.GONE);
            }
            //5.处理赞
            holder.praise.setImageResource(dynamic.isPraised() ? R.drawable.icon_like : R.drawable.icon_like_normal);
            holder.praise.setTag(R.integer.key_dynamic_praised, dynamic.isPraised());
            holder.praise.setTag(R.integer.key_dynamic_id, dynamic.getId());
            holder.praise.setOnClickListener(v -> {
                if (!(Boolean) v.getTag(R.integer.key_dynamic_praised)) {
                    if (LoginManager.getInstance().isLoginValidate()) {
                        holder.praise.setImageResource(R.drawable.icon_like);
                        v.setTag(true);
                        dynamic.setPraised(true);
                        //动画
                        v.clearAnimation();
                        v.startAnimation(new LikeAnimation(2.0f, 0.8f, 1.0f));
                        //数量
                        String num = holder.praiseNum.getText().toString();
                        if (ValidateUtil.isValidate(num)) {
                            Integer i_num = Integer.valueOf(num);
                            holder.praiseNum.setText("" + (i_num + 1));
                            dynamic.setPraise((i_num + 1));
                        }
                        //发送请求
                        mViewModel.requestPraise((Integer) v.getTag(R.integer.key_dynamic_id));

                    } else {
                        JToast.show(R.string.tip_login, mContext);
                    }
                }
            });

            //6.处理评论
            if (ValidateUtil.isValidate(dynamic.getComments())) {
                holder.mCommentRecycler.setVisibility(View.VISIBLE);
                initCommentRecyclerView(holder.mCommentRecycler, dynamic.getComments());
            } else {
                holder.mCommentRecycler.setVisibility(View.GONE);
            }
            //7.地址
            String city = dynamic.getCity();
            if (ValidateUtil.isValidate(city) && city.endsWith("市")) {
                city = city.substring(0, city.length() - 1);
            }

            String district = dynamic.getDistrict();
            if (ValidateUtil.isValidate(district) && (district.endsWith("区")
                    || district.endsWith("路")
                    || district.endsWith("镇")
                    || district.endsWith("县"))) {
                district = district.substring(0, district.length() - 1);
            }
            if (ValidateUtil.isValidate(city)) {
                holder.address.setVisibility(View.VISIBLE);
                if (ValidateUtil.isValidate(district)) {
                    holder.address.setText(String.format(ResHelper.getString(R.string.dynamic_address)
                            , city, district));
                } else {
                    holder.address.setText(city);
                }
            } else {
                holder.address.setVisibility(View.GONE);
            }

            //8.评论数
            holder.commentNum.setText(NumberUtil.getFormatNumber(dynamic.getComment()));
            //9.点赞数
            holder.praiseNum.setText(NumberUtil.getFormatNumber(dynamic.getPraise()));
            //10.点击进行评论
            holder.comment.setTag(dynamic.getId());
            holder.comment.setOnClickListener(v -> {
                if (LoginManager.getInstance().isLoginValidate()) {
                    RxBusManager.post(EventConstant.COMMENT_DYNAMIC, dynamic.getId());
                } else {
                    JToast.show(R.string.tip_login, mContext);
                }
            });
            //11.  时间
            String time = dynamic.getCreated();
            if (ValidateUtil.isValidate(time)) {
                long timestamp = System.currentTimeMillis() - TimeUtil.getTempTime(time);
                holder.time.setText(TimeUtil.refreshUpdatedAtValue(timestamp));
            }

        } else {
            FooterView footerView = (FooterView) viewHolder.itemView;
            if (mIsCompleted || (ValidateUtil.isValidate(mList) && mList.size() % 10 != 0)) {
                footerView.hideView();
            } else {
                footerView.showLoadMoreTv();
                footerView.setOnLoadMoreClickListener(view -> {
                    if (mOnDynamicClickListener != null) {
                        mOnDynamicClickListener.onLastItemClickListener(footerView);
                    }
                });
            }
        }
    }

    //初始化图片
    private void initPicRecyclerView(RecyclerView recyclerView, ArrayList<String> images, String dynamic) {
        //1.设置尺寸
        LinearLayout.LayoutParams params;
        GridLayoutManager layoutManager;
        if (images.size() > 3) {
            int size = images.size();
            int rows = size % 3 == 0 ? size / 3 : (size / 3 + 1);
            params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (int) (rows * mPicItemSize + (rows - 1) * PixelUtil.dp2px(4.f))
            );
            layoutManager = new GridLayoutManager(mContext, 3);
        } else if (images.size() == 3) {
            params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    mPicItemSize
            );
            layoutManager = new GridLayoutManager(mContext, 3);
        } else if (images.size() == 2) {
            int itemSize = (int) (0.86 * mPicWidth) / 2;
            params = new LinearLayout.LayoutParams(
                    2 * itemSize + (int) PixelUtil.dp2px(4.f),
                    itemSize
            );
            layoutManager = new GridLayoutManager(mContext, 2);
        } else {
            params = new LinearLayout.LayoutParams(
                    (int) (0.7 * mPicWidth),
                    (int) (0.7 * mPicWidth)
            );
            layoutManager = new GridLayoutManager(mContext, 1);
        }
        params.topMargin = (int) PixelUtil.dp2px(5.f);
        recyclerView.setLayoutParams(params);
        recyclerView.setLayoutManager(layoutManager);
        //2.分割
        try {
            Class clazz = recyclerView.getClass();
            Field field = clazz.getDeclaredField("mItemDecorations");
            field.setAccessible(true);
            ArrayList<RecyclerView.ItemDecoration> list = (ArrayList<RecyclerView.ItemDecoration>) field.get(recyclerView);
            if (list != null && list.size() <= 0) {
                GridRecyclerDecoration decoration = new GridRecyclerDecoration((int) PixelUtil.dp2px(4.f), 0xffffffff);
                recyclerView.addItemDecoration(decoration);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //3.设置适配器
        DynamicPicAdapter adapter = new DynamicPicAdapter(mContext, images);
        adapter.setOnItemClickListener(view -> {
            Integer position = (Integer) view.getTag();
            DynamicDetailActivity.launch(mContext, images, dynamic, position);
        });
        recyclerView.setAdapter(adapter);
    }

    //初始化评论
    private void initCommentRecyclerView(RecyclerView recyclerView, List<DynamicComment> comments) {
        //1.设置尺寸
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        //3.设置适配器
        DynamicCommentAdapter adapter = new DynamicCommentAdapter(mContext, comments);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        //如果mList的长度大于0并且能够被10整除，那么就有footerView
        return ValidateUtil.isValidate(mList) ? mList.size() + 1 : 0;
    }


    public void setOnDynamicClickListener(OnDynamicClickListener onDynamicClickListener) {
        this.mOnDynamicClickListener = onDynamicClickListener;
    }

    public interface OnDynamicClickListener {
        //3.当加载更多被点击
        void onLastItemClickListener(View view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.user_avatar)
        ImageView userAvatar;
        @InjectView(R.id.user_nickname)
        TextView userName;
        @InjectView(R.id.content)
        TextView content;
        @InjectView(R.id.dynamic_list_pic_recycler_view)
        RecyclerView mPictureRecycler;
        @InjectView(R.id.dynamic_comment)
        ImageView comment;
        @InjectView(R.id.dynamic_praise)
        ImageView praise;
        @InjectView(R.id.dynamic_publish_time)
        TextView time;
        @InjectView(R.id.dynamic_list_comm_recycler_view)
        RecyclerView mCommentRecycler;
        @InjectView(R.id.user_address)
        TextView address;
        @InjectView(R.id.dynamic_comment_num)
        TextView commentNum;
        @InjectView(R.id.dynamic_praise_num)
        TextView praiseNum;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
