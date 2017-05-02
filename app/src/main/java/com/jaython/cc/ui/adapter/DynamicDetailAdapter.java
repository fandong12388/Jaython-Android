package com.jaython.cc.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.jaython.cc.R;
import com.tiny.loader.TinyImageLoader;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;

/**
 * time:2017/2/10
 * description:
 *
 * @author fandong
 */
public class DynamicDetailAdapter extends PagerAdapter {
    private List<String> mImages;
    private Context mContext;

    public DynamicDetailAdapter(Context context, ArrayList<String> images) {
        this.mContext = context;
        this.mImages = images;
    }

    @Override
    public int getCount() {
        return mImages.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(mContext);
        String url = mImages.get(position);
        if (!url.startsWith("http") && !url.startsWith("file")) {
            url = "file://" + url;
        }

        TinyImageLoader.create(url)
                .setDefaultRes(R.drawable.logo_loading)
                .into(photoView);

        container.addView(photoView);
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
