package com.zzhserver.main.group.create;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zzhserver.main.group.GroupModel;
import com.zzhserver.global.Const;
import com.zzhserver.pojo.bean.FileBean;
import com.zzhserver.pojo.event.EventGroup;
import com.zzhserver.utils.DownUploadUtil;
import com.zzhserver.utils.ImageUtils;
import com.zzhserver.utils.LogUtils;
import com.zzhserver.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import top.zibin.luban.OnCompressListener;

/**
 * Created by Administrator on 2017/11/5 0005.
 */

public class GroupCreatePresenter {
    private String headPic = "";
    private GroupCreateListener view;

    public GroupCreatePresenter(GroupCreateListener view) {
        this.view = view;
        EventBus.getDefault().register(this);
    }

    public void remove() {
        EventBus.getDefault().unregister(this);
    }

    public void createGroup(String name) {
        GroupModel.getInstance().createGroup(name, headPic);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventGroup msg) {
        switch (msg.event) {
            case Const.EVENT_CREATE_GROUP_SUCCESS:
                ToastUtils.show("建立群组成功");
                view.finish();
                break;
            case Const.EVENT_CREATE_GROUP_FAIL:
                ToastUtils.show("建立群组失败");
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
                DownUploadUtil.getInstance().upload( Const.UP_FILE_URL, file.getAbsolutePath(), new DownUploadUtil.OnUploadListener() {
                    @Override
                    public void onUploadSuccess(String result) {
                        try {
                            LogUtils.i("onUploadSuccess:" + result);
                            FileBean fileBean = new Gson().fromJson(result, FileBean.class);
                            LogUtils.i("onUploadSuccess:fileBean=" + fileBean.toString());
                            if (fileBean.code == 0) {
                                headPic = fileBean.data;
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
