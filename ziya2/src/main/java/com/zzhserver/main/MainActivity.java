package com.zzhserver.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;

import com.zzhserver.R;
import com.zzhserver.global.BaseActivity;
import com.zzhserver.main.user.nearest.ChatNearestFragment;
import com.zzhserver.main.setting.SettingFragment;
import com.zzhserver.main.user.ContactFragment;
import com.zzhserver.utils.LogUtils;

public class MainActivity extends BaseActivity {

    private ViewPager mViewPager;
    private Toolbar toolbar;
    private RadioGroup rg_bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtils.i("keyCode = "+keyCode);
        if(keyCode == KeyEvent.KEYCODE_BACK){
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        rg_bottom = findViewById(R.id.rg_bottom);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        rg_bottom.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioButtonId = group.getCheckedRadioButtonId();
                switch (radioButtonId) {
                    case R.id.rb_chat:
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.rb_user:
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.rb_setting:
                        mViewPager.setCurrentItem(2);
                        break;
                }
            }
        });
    }

    private void initFragment() {
        Fragment chatFragment = ChatNearestFragment.Companion.newInstance();
        Fragment contactFragment = ContactFragment.Companion.newInstance();
        Fragment settingFragment = SettingFragment.Companion.newInstance();
        Fragment[] fragments = new Fragment[]{chatFragment, contactFragment, settingFragment};
        MainFragmentAdapter fragmentAdapter = new MainFragmentAdapter(getSupportFragmentManager(), fragments);
        // 设置适配器
        mViewPager = findViewById(R.id.viewpager_main);
        mViewPager.setAdapter(fragmentAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.setOffscreenPageLimit(3);

        //页面变化时的监听器
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        rg_bottom.check(R.id.rb_chat);
                        break;
                    case 1:
                        rg_bottom.check(R.id.rb_user);
                        break;
                    case 2:
                        rg_bottom.check(R.id.rb_setting);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

}
