package com.jaython.cc.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaython.cc.R;
import com.jaython.cc.bean.ActionGroup;

import java.util.List;

/**
 * time:2017/1/13
 * description:
 *
 * @author fandong
 */
public class ActionPagerAdapter extends PagerAdapter {
    private Context mContext;

    //需要显示的数据
    private List<ActionGroup> mGroups;

    private LayoutInflater mLayoutInflater;

    public ActionPagerAdapter(Context context, List<ActionGroup> groups) {
        this.mContext = context;
        this.mGroups = groups;
        this.mLayoutInflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mGroups.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //1.初始化
        View view = mLayoutInflater.inflate(R.layout.vw_action_pager_item, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.action_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        ActionAdapter adapter = new ActionAdapter(mContext, mGroups.get(position).getActions());
        recyclerView.setAdapter(adapter);
        //2.添加
        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
