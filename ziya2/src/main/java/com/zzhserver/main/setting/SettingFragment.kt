package com.zzhserver.main.setting

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zzhserver.R
import com.zzhserver.global.Const
import com.zzhserver.main.InfoModel
import com.zzhserver.manager.GrpcManager
import com.zzhserver.pojo.event.EventSet
import com.zzhserver.ui.DialogEdit
import com.zzhserver.utils.*
import kotlinx.android.synthetic.main.fragment_setting.*
import me.iwf.photopicker.PhotoPicker
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File


class SettingFragment : Fragment(), View.OnClickListener {

    private lateinit var mView: View
    private var firstCreate = true
    companion object {
        fun newInstance(): SettingFragment {
            return SettingFragment()
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_setting, container, false)
        EventBus.getDefault().register(this)
        return mView
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        if (firstCreate) {
            firstCreate = false
            initView()
            updateData()
        }
    }

    private fun updateData() {
        LogUtils.i("updateData()")
        if (!TextUtils.isEmpty(InfoModel.name)) {
            item_name.text4Text = InfoModel.name
            item_number.text4Text = InfoModel.uid.toString()
        }
        ImageUtils.load(InfoModel.headPic, iv_head, ImageUtils.USER_PIC)
    }

    private fun initView() {
        iv_head.setOnClickListener(this)
        item_name.setOnClickListener(this)
        item_number.setOnClickListener(this)
        item_password.setOnClickListener(this)
        item_qrcode.setOnClickListener(this)
        item_about.setOnClickListener(this)
        item_logout.setOnClickListener(this)
        item_qrcode.setText4Bg(R.mipmap.ic_qrcore)
        item_password.setText4Bg(R.mipmap.ic_mode_edit)
        item_about.setText4Bg(R.mipmap.ic_info)
        item_logout.setText4Bg(R.mipmap.ic_logout)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_head -> activity?.let {
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setShowCamera(true)
                        .setPreviewEnabled(false)
                        .start(it, this, PhotoPicker.REQUEST_CODE)
            }
            R.id.item_name -> activity?.let {
                val dialog = DialogEdit.builder(it)
                dialog.msg("修改你的昵称", "修改昵称", "").no().yes(DialogInterface.OnClickListener { dialogInterface, i ->
                    dialog.getName()?.let {
                        if (it.isEmpty()) {
                            ToastUtils.show("昵称不能为空")
                        } else {
                            SettingModel.modifyName(it)
                        }
                    }
                }).build()
            }
            R.id.item_password -> {
            }
            R.id.item_qrcode -> GrpcManager.getInstance().sendTest()
            R.id.item_about -> {
            }
            R.id.item_logout -> InfoModel.logout()
        }//iv_head.setImageResource(R.mipmap.ic_back_white);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        LogUtils.i("resultCode = $resultCode,requestCode = $requestCode")
        if (resultCode == Activity.RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            LogUtils.i("选择图片")
            //选取图片
            data?.let {
                val photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS)
                val path = photos[0]
                LogUtils.i("path = " + path + ",file.length=" + File(path).length())
                startActivityForResult(ImageUtils.cropImage(path), 1011)
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == 1011) {//剪切图片后
            LogUtils.i("OK CACHE_CROP_PATH=" + ImageUtils.CACHE_CROP_PATH)
            SettingModel.compUpload(ImageUtils.CACHE_CROP_PATH)//成功后发出图片地址的消息
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessage(msg: EventSet) {
        when (msg.event) {
            Const.EVENT_MODIFY_USER_INFO_FAIL -> ToastUtils.show("修改失败")
            Const.EVENT_MODIFY_USER_INFO_SUCCESS -> {
                updateData()
                ToastUtils.show("修改成功")
            }
        }
    }
}
