package com.zzhserver.main.group.detail;


import android.content.Intent;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zzhserver.R;
import com.zzhserver.global.Const;
import com.zzhserver.main.group.GroupModel;
import com.zzhserver.main.InfoModel;
import com.zzhserver.main.user.UserDetailActivity;
import com.zzhserver.pojo.bean.FileBean;
import com.zzhserver.pojo.db.DGroup;
import com.zzhserver.pojo.db.UserGroup;
import com.zzhserver.pojo.event.EventGroup;
import com.zzhserver.utils.AppUtils;
import com.zzhserver.utils.DownUploadUtil;
import com.zzhserver.utils.ImageUtils;
import com.zzhserver.utils.LogUtils;
import com.zzhserver.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

import top.zibin.luban.OnCompressListener;

/**
 * Created by Administrator on 2017/11/5 0005.
 */

public class GroupDetailPresenter {
    private GroupDetailAdapter adapter;
    private GroupDetailListener view;
    private DGroup group;
    private ArrayList<UserGroup> list = new ArrayList<>();

    public GroupDetailPresenter(GroupDetailListener view,int gid) {
        this.view = view;
        EventBus.getDefault().register(this);
        this.group = GroupModel.getInstance().getGroupArray().get(gid);
        adapter = new GroupDetailAdapter(R.layout.item_detail, list);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //详细资料
                UserGroup bean = list.get(position);
                //UserBean userBean = UserModel.getInstance().getUserArray().get(bean.getUid());
                Intent intent = new Intent(AppUtils.getAppContext(), UserDetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Const.TAG_ID, bean.getUid());
                AppUtils.getAppContext().startActivity(intent);
            }
        });
        adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                groupDelUser(list.get(position).getUid());
                return true;
            }
        });
        init();
    }

    private void init() {
        adapter.addData(GroupModel.getInstance().getGroupUserList(group.getGid()));
        LogUtils.i("groupUserList = " + list);
    }

    public DGroup getGroup() {
        group = GroupModel.getInstance().getGroupArray().get(group.getGid());
        return group;
    }

    public GroupDetailAdapter getAdapter() {
        return adapter;
    }

    public void remove() {
        EventBus.getDefault().unregister(this);
    }

    public void groupDelUser(int tagId) {
        GroupModel.getInstance().groupDelUser(group.getGid(), tagId);
    }

    public void modifyGroupHeadPic(String headPic) {
        GroupModel.getInstance().modifyGroup(group.getGid(), group.getName(), headPic);
    }

    public void modifyGroupName(String name) {
        GroupModel.getInstance().modifyGroup(group.getGid(), name, group.getHeadPic());
    }

    public void deleteGroup() {
        if (group.getAdmin() == InfoModel.INSTANCE.getUid()) {//自己是群主,删除群组
            GroupModel.getInstance().removeGroup(group.getGid());
        } else {//自己不是群组,退出群组
            GroupModel.getInstance().quitGroup(group.getGid());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventGroup msg) {
        switch (msg.event) {
            case Const.EVENT_GET_GROUP_USER_LIST:
                list = GroupModel.getInstance().getGroupUserList(group.getGid());
                adapter.replaceData(list);
                break;
            case Const.EVENT_MODIFY_GROUP_SUCCESS:
                view.modify();
                ToastUtils.show("更新成功");
                break;
            case Const.EVENT_MODIFY_GROUP_FAIL:
                ToastUtils.show("更新失败");
                break;
            case Const.EVENT_DELETE_GROUP_SUCCESS:
                ToastUtils.show("移除群组成功");
                view.finish();
                break;
            case Const.EVENT_DELETE_GROUP_FAIL:
                ToastUtils.show("移除群组失败");
                break;
            case Const.EVENT_GROUP_DEL_SUCCESS:
                ToastUtils.show("删除成功");
                break;
        }
    }

    //压缩图片,上传图片,发送图片地址
    public void compUpload(String path) {//压缩图片
        ImageUtils.showResult(new File(path));//查看压缩前的大小
        ImageUtils.compress(path, new OnCompressListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(final File file) {
                DownUploadUtil.getInstance().upload(Const.UP_FILE_URL, file.getAbsolutePath(), new DownUploadUtil.OnUploadListener() {
                    @Override
                    public void onUploadSuccess(String result) {
                        try {
                            LogUtils.i("onUploadSuccess:" + result);
                            FileBean fileBean = new Gson().fromJson(result, FileBean.class);
                            LogUtils.i("onUploadSuccess:fileBean=" + fileBean.toString());
                            if (fileBean.code == 0) {
                                String headPic = fileBean.data;
                                modifyGroupHeadPic(headPic);
                                ToastUtils.show("上传头像成功");
                            }
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onUploading(int progress) {
                        LogUtils.i("progress:" + progress);
                        //progressBar.setProgress(progress);
                    }

                    @Override
                    public void onUploadFailed() {
                        LogUtils.i("onUploadFailed!!!!");
                    }
                });
                ImageUtils.showResult(file);//查看压缩后的大小
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }
}
