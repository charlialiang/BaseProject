package com.zzhserver.main.setting

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.zzhserver.main.InfoModel
import com.zzhserver.global.Const
import com.zzhserver.manager.GrpcManager
import com.zzhserver.pojo.bean.FileBean
import com.zzhserver.pojo.db.DUser
import com.zzhserver.pojo.event.EventSet
import com.zzhserver.utils.DownUploadUtil
import com.zzhserver.utils.ImageUtils
import com.zzhserver.utils.LogUtils

import org.greenrobot.eventbus.EventBus
import top.zibin.luban.OnCompressListener
import java.io.File

/**
 * Created by Administrator on 2017/12/30 0030.
 */

object SettingModel {
    private val gson = Gson()

    fun modifyHeadPic(picUrl: String) {
        GrpcManager.getInstance().setMyUserInfo(InfoModel.name, picUrl)
    }

    fun modifyName(name: String) {
        GrpcManager.getInstance().setMyUserInfo(name, InfoModel.headPic)
    }

    fun retModify(msg: String) {
        if (msg == Const.FAIL) {
            EventBus.getDefault().post(EventSet(Const.EVENT_MODIFY_USER_INFO_FAIL))
        } else {
            try {
                val user = gson.fromJson(msg, DUser::class.java)
                InfoModel.setMyUser(user)
                EventBus.getDefault().post(EventSet(Const.EVENT_MODIFY_USER_INFO_SUCCESS))
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    //压缩图片,上传图片,发送图片地址
    fun compUpload(path: String) {//压缩图片
        ImageUtils.showResult(File(path))//查看压缩前的大小
        ImageUtils.compress(path, 20, 0, 90, object : OnCompressListener {
            override fun onStart() {

            }

            override fun onSuccess(file: File) {
                //val pathName = EncryptUtils.MD5("" + System.currentTimeMillis())
                DownUploadUtil.getInstance().upload(Const.UP_FILE_URL, file.absolutePath, object : DownUploadUtil.OnUploadListener {
                    override fun onUploadSuccess(result: String) {
                        try {
                            //LogUtils.i("onUploadSuccess:" + result)
                            val fileBean = Gson().fromJson(result, FileBean::class.java)
                            LogUtils.i("onUploadSuccess:fileBean=" + fileBean.toString())
                            if (fileBean.code == 0) {
                                SettingModel.modifyHeadPic(fileBean.data)
                                //FileUtils.deleteFile(File(ImageUtils.IMAGE_DIR))
                            }
                        } catch (e: JsonSyntaxException) {
                            e.printStackTrace()
                        }

                    }

                    override fun onUploading(progress: Int) {
                        LogUtils.i("progress:" + progress)
                    }

                    override fun onUploadFailed() {
                        LogUtils.i("onUploadFailed!!!!")
                    }
                })
            }

            override fun onError(e: Throwable) {

            }
        })
    }
}