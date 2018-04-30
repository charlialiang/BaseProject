package com.zzhserver.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import com.zzhserver.utils.LogUtils;

/**
 * Created by Administrator on 2016/6/27 0027.
 */
public class MainFragmentAdapter extends FragmentPagerAdapter {
    private FragmentManager fm;
    private Fragment[] fragments;
    private boolean[] fragmentsUpdateFlag;

    public MainFragmentAdapter(FragmentManager fm, Fragment[] fragments) {
        super(fm);
        this.fm = fm;
        this.fragments = fragments;
        fragmentsUpdateFlag = new boolean[fragments.length];
        for (int i = 0; i < fragments.length; i++) {
            fragmentsUpdateFlag[i] = false;
        }
    }


    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = fragments[position % fragments.length];
        LogUtils.i("fragment getItem:position=" + position + ",fragment:"
                + fragment.getClass().getName() + ",fragment.tag="
                + fragment.getTag());
        return fragments[position % fragments.length];
    }


    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //得到缓存的fragment
        Fragment fragment = (Fragment) super.instantiateItem(container,
                position);
        //得到tag，这点很重要
        String fragmentTag = fragment.getTag();
        if (fragmentsUpdateFlag[position % fragmentsUpdateFlag.length]) {
            //如果这个fragment需要更新
            FragmentTransaction ft = fm.beginTransaction();
            //移除旧的fragment
            ft.remove(fragment);
            //换成新的fragment
            fragment = fragments[position % fragments.length];
            //添加新fragment时必须用前面获得的tag，这点很重要
            ft.add(container.getId(), fragment, fragmentTag);
            ft.attach(fragment);
            ft.commit();
            //复位更新标志
            fragmentsUpdateFlag[position % fragmentsUpdateFlag.length] = false;
        }


        return fragment;
    }
}


