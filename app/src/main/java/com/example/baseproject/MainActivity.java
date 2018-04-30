package com.example.baseproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.baseproject.manager.ObjectBoxManager;
import com.example.baseproject.mvp.BaseMvpActivity;
import com.example.baseproject.mvp.presenter.MainPresenter;
import com.example.baseproject.mvp.view.MainView;
import com.example.baseproject.pojo.UserInfoBean;
import com.example.baseproject.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;

public class MainActivity extends BaseMvpActivity<MainView,MainPresenter> implements MainView{
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected MainPresenter createPresener() {
        return new MainPresenter();
    }

    @Override
    protected MainView createView() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initToolBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    private void initToolBar(){
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ToastUtils.show(item.getTitle().toString());
                return false;
            }
        });
    }

    private void objectBoxTest(){
        UserInfoBean userInfoBean = new UserInfoBean();
        userInfoBean.setUserId(100);
        Box box = ObjectBoxManager.getInstance().getBoxStore(ObjectBoxManager.USERINFOCLASS);
        box.put(userInfoBean);
        ObjectBoxManager.getInstance().getUserInfoByUserId(100);
    }
}
