package com.zzhserver.main.group.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.zzhserver.R;
import com.zzhserver.global.BaseActivity;
import com.zzhserver.main.group.create.GroupCreateActivity;


public class GroupActivity extends BaseActivity {
    private GroupPresenter presenter;
    private RecyclerView rv_group;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        initMvp();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.remove();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(mActivity, GroupCreateActivity.class));
                return true;
            }
        });
        rv_group = findViewById(R.id.rv_group);
        rv_group.setLayoutManager(new LinearLayoutManager(mActivity));
        rv_group.setAdapter(presenter.getGroupAdapter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group, menu);
        return true;
    }

    private void initMvp() {
        //MVP的视图层
        //MVP的P层
        presenter = new GroupPresenter();
    }

}
