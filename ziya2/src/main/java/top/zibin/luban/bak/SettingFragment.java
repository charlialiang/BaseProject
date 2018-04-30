/*
package top.zibin.luban.bak;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sora.R;
import com.sora.code.InfoModel;
import com.sora.code.setting.SettingModel;
import com.sora.global.Const;
import com.sora.manager.GrpcManager;
import com.sora.pojo.bean.FileBean;
import com.sora.pojo.event.EventSet;
import com.sora.ui.ItemView;
import com.sora.utils.DownUploadUtil;
import com.sora.utils.EncryptUtils;
import com.sora.utils.ImageUtils;
import com.sora.utils.LogUtils;
import com.sora.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

import me.iwf.photopicker.PhotoPicker;
import top.zibin.luban.OnCompressListener;


public class SettingFragment extends Fragment implements View.OnClickListener {
    private View mView;
    private ItemView item_name;
    private ItemView item_number;
    private ItemView item_password;
    private ItemView item_qrcode;
    private ItemView item_about;
    private ItemView item_logout;
    private ImageView iv_head;
    private FragmentActivity mActivity;


    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = getActivity();
        mView = inflater.inflate(R.layout.fragment_setting, container, false);
        initView();
        initData();
        EventBus.getDefault().register(this);
        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    private void initData() {
        if (!TextUtils.isEmpty(InfoModel.getInstance().getName())) {
            item_name.setText4Text(InfoModel.getInstance().getName());
            item_number.setText4Text(String.valueOf(InfoModel.getInstance().getUid()));
        }
        ImageUtils.load(InfoModel.getInstance().getHeadPic(), iv_head, ImageUtils.USER_PIC);
    }

    private void initView() {
        iv_head = mView.findViewById(R.id.iv_head);
        item_name = mView.findViewById(R.id.item_name);
        item_number = mView.findViewById(R.id.item_number);
        item_password = mView.findViewById(R.id.item_password);
        item_qrcode = mView.findViewById(R.id.item_qrcode);
        item_about = mView.findViewById(R.id.item_about);
        item_logout = mView.findViewById(R.id.item_logout);
        iv_head.setOnClickListener(this);
        item_name.setOnClickListener(this);
        item_number.setOnClickListener(this);
        item_password.setOnClickListener(this);
        item_qrcode.setOnClickListener(this);
        item_about.setOnClickListener(this);
        item_logout.setOnClickListener(this);
        item_qrcode.setText4Bg(R.mipmap.ic_qrcore);
        item_password.setText4Bg(R.mipmap.ic_mode_edit);
        item_about.setText4Bg(R.mipmap.ic_info);
        item_logout.setText4Bg(R.mipmap.ic_logout);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_head:
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setShowCamera(true)
                        .setPreviewEnabled(false)
                        .start(mActivity, this, PhotoPicker.REQUEST_CODE);
                break;
            case R.id.item_name:
                View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_group_name, null);
                final EditText et_name = view.findViewById(R.id.et_name);
                final AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity)
                        .setMessage("修改昵称").setView(view)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String name = et_name.getText().toString().trim();
                                if (TextUtils.isEmpty(name)) {
                                    ToastUtils.show("昵称不能为空");
                                    return;
                                }
                                SettingModel.getInstance().modifyName(name);
                            }
                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                dialog.create().show();
                break;
            case R.id.item_number:

                break;
            case R.id.item_password:

                break;
            case R.id.item_qrcode:
                GrpcManager.getInstance().sendTest();
                break;
            case R.id.item_about:
                //iv_head.setImageResource(R.mipmap.ic_back_white);
                break;
            case R.id.item_logout:
                InfoModel.getInstance().logout();
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            LogUtils.i("data == null");
            return;
        }
        LogUtils.i("resultCode = " + resultCode + ",requestCode = " + requestCode);
        if (resultCode == Activity.RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            LogUtils.i("选择图片");
            //选取图片
            if (data != null) {
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                String path = photos.get(0);
                LogUtils.i("path = " + path + ",file.length=" + new File(path).length());
                ImageUtils.showResult(new File(path));
                startActivityForResult(ImageUtils.cropImage(path), 1011);
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == 1011) {//剪切图片后
            LogUtils.i("OK CACHE_CROP_PATH=" + ImageUtils.CACHE_CROP_PATH);
            compUpload(ImageUtils.CACHE_CROP_PATH);//压缩上传图片,成功后发出图片地址的消息
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
            public void onSuccess(File file) {
                String pathName = EncryptUtils.MD5("" + System.currentTimeMillis());
                DownUploadUtil.getInstance().upload(Const.UP_FILE_URL, file.getAbsolutePath(), new DownUploadUtil.OnUploadListener() {
                    @Override
                    public void onUploadSuccess(String result) {
                        try {
                            LogUtils.i("onUploadSuccess:" + result);
                            FileBean fileBean = new Gson().fromJson(result, FileBean.class);
                            LogUtils.i("onUploadSuccess:fileBean=" + fileBean.toString());
                            if (fileBean.code == 0) {
                                SettingModel.getInstance().modifyHeadPic(fileBean.data);
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
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventSet msg) {
        switch (msg.event) {
            case Const.EVENT_MODIFY_USER_INFO_FAIL:
                ToastUtils.show("修改失败");
                break;
            case Const.EVENT_MODIFY_USER_INFO_SUCCESS:
                initData();
                ToastUtils.show("修改成功");
                break;
        }
    }
}
*/
