package com.zzhserver.main.group.detail;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zzhserver.R;
import com.zzhserver.global.BaseActivity;
import com.zzhserver.global.Const;
import com.zzhserver.main.InfoModel;
import com.zzhserver.main.user.list.UserListActivity;
import com.zzhserver.pojo.db.DGroup;
import com.zzhserver.ui.ItemView;
import com.zzhserver.utils.ImageUtils;
import com.zzhserver.utils.LogUtils;
import com.zzhserver.utils.ToastUtils;

import java.util.ArrayList;

import me.iwf.photopicker.PhotoPicker;


public class GroupDetailActivity extends BaseActivity implements View.OnClickListener {
    private GroupDetailPresenter presenter;
    private Toolbar toolbar;
    private RecyclerView rv_detail;
    private ItemView item_head;
    private ItemView item_name;
    private ItemView item_gid;
    private Button btn_delete;
    private int gid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        gid = getIntent().getIntExtra(Const.TAG_ID, 0);
        initMvp();
        initView();
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_req, menu);
        return true;
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
                startActivity(new Intent(mActivity, UserListActivity.class).putExtra(Const.TAG_ID, gid));
                return true;
            }
        });
        rv_detail = findViewById(R.id.rv_detail);
        rv_detail.setLayoutManager(new GridLayoutManager(mActivity, 4));
        item_head = findViewById(R.id.item_head);
        item_name = findViewById(R.id.item_name);
        item_gid = findViewById(R.id.item_gid);
        btn_delete = findViewById(R.id.btn_delete);
        item_head.setOnClickListener(this);
        item_name.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
    }

    private void initData() {
        DGroup group = presenter.getGroup();
        rv_detail.setAdapter(presenter.getAdapter());
        LogUtils.i("tagGroup = " + group);
        ImageUtils.load(group.getHeadPic(),item_head.getIcon(),ImageUtils.GROUP_PIC);
        item_name.setText4Text(group.getName());
        item_gid.setText4Text("" + group.getGid());
        if (group.getAdmin() == InfoModel.INSTANCE.getUid()) {
            btn_delete.setText("删除群组");
        } else {
            btn_delete.setText("退出群组");
        }
    }

    private void initMvp() {
        //MVP的视图层
        GroupDetailListener view = new GroupDetailListener() {
            @Override
            public void finish() {
                mActivity.finish();
            }

            @Override
            public void modify() {
                initData();
            }

            @Override
            public void show() {

            }

            @Override
            public void dismiss() {

            }
        };
        //MVP的P层
        presenter = new GroupDetailPresenter(view, gid);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_head:
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setShowCamera(true)
                        .setPreviewEnabled(false)
                        .start(mActivity, PhotoPicker.REQUEST_CODE);
                break;
            case R.id.item_name:
                View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_edit, null);
                final EditText et_name = view.findViewById(R.id.et_name);
                final AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity)
                        .setMessage("群组名称").setView(view)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String groupName = et_name.getText().toString().trim();
                                if (TextUtils.isEmpty(groupName)) {
                                    ToastUtils.show("群组名称不能为空");
                                    return;
                                }
                                presenter.modifyGroupName(groupName);
                            }
                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                dialog.create().show();
                break;
            case R.id.btn_delete:
                presenter.deleteGroup();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.i("resultCode = " + resultCode + ",requestCode = " + requestCode);
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            //选取图片
            if (data != null) {
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                presenter.compUpload(photos.get(0));//压缩上传图片,成功后发出图片地址的消息
            }
        }
    }
}
