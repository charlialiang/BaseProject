/*
package com.sora.model;

import android.text.TextUtils;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCustomSignerCredentialProvider;
import com.alibaba.sdk.android.oss.common.utils.OSSUtils;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.sora.utils.AppUtils;
import com.sora.utils.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static junit.framework.Assert.assertNotNull;

*/
/**
 * Created by Administrator on 2017/12/10 0010.
 *//*


public class FileManager {

    public interface FileDownListener {
        void onProgress(int progress);
        void onSuccess(String path);
        void onFailure();
    }
    public interface FileUpListener {
        void onProgress(int progress);
        void onSuccess();
        void onFailure();
    }

    private static FileManager instance;

    private FileManager() {
    }

    public static FileManager getInstance() {
        if (instance == null) {
            synchronized (FileManager.class) {
                if (instance == null) {
                    instance = new FileManager();
                }
            }
        }
        return instance;
    }

    public static final String FILE_PATH = "http://zzh-file.oss-cn-shenzhen.aliyuncs.com/";
    public static final String ENDPOINT = "http://oss-cn-shenzhen.aliyuncs.com/";
    public static final String ACCESS_KEY_ID = "LTAInPVqXsxM6hPN";
    public static final String SECRET_KEY_ID = "nYJkQr246EDv7e5DLCVsKdho0eSDmg";
    public static final String BUCKET_NAME = "zzh-file";
    public static final String UPLOAD_PIC_PATH = "pic/";
    public static final String UPLOAD_RECORD_PATH = "record/";

    private ClientConfiguration conf = new ClientConfiguration();

    public void initConfig() {
        OSSLog.enableLog();  //调用此方法即可开启日志
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
    }

    public OSS getOss() {
        initConfig();
        final OSSCredentialProvider credentialProvider = new OSSCustomSignerCredentialProvider() {
            @Override
            public String signContent(String content) {
                String signature = "";
                try {
                    signature = OSSUtils.sign(ACCESS_KEY_ID, SECRET_KEY_ID, content);
                    assertNotNull(signature);
                    OSSLog.logDebug(signature);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return signature;
            }
        };
        OSS oss = new OSSClient(AppUtils.getAppContext(), ENDPOINT, credentialProvider, conf);

        return oss;
    }

    public void upFile(String upFileName, String localFile, final FileUpListener listener) {
        if (TextUtils.isEmpty(upFileName)) {
            LogUtils.i("AsyncPutImage" + "ObjectNull");
            return;
        }

        File file = new File(localFile);
        if (!file.exists()) {
            LogUtils.i("AsyncPutImage" + "FileNotExist");
            return;
        }
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(BUCKET_NAME, upFileName, localFile);
        OSSProgressCallback<PutObjectRequest> callback2 = new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                int progress = (int) (100 * currentSize / totalSize);
                LogUtils.i("上传进度: " + progress + "%");
                listener.onProgress(progress);
            }
        };
        // 异步上传时可以设置进度回调
        put.setProgressCallback(callback2);
        getOss().asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                LogUtils.i("上传OK");
                listener.onSuccess();
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                LogUtils.i("上传失败");
                if (clientException != null) { // 本地异常如网络异常等
                    clientException.printStackTrace();
                }
                if (serviceException != null) {
                    serviceException.printStackTrace(); // 服务异常
                }
                listener.onFailure();
            }
        });
    }

    public void downFile(String downFileName, final FileDownListener listener) {
        if (TextUtils.isEmpty(downFileName)) {
            LogUtils.i("AsyncPutImage" + "ObjectNull");
            return;
        }
        final File file = AppUtils.getAppContext().getFilesDir();
        if (!file.exists()) {
            file.mkdirs();
            LogUtils.i("AsyncPutImage" + "FileNotExist");
            return;
        }
        final String savePath = file.getAbsolutePath() + "/" + System.currentTimeMillis();
        GetObjectRequest get = new GetObjectRequest(BUCKET_NAME, downFileName);
        // 构造上传请求
        OSSProgressCallback<GetObjectRequest> callback2 = new OSSProgressCallback<GetObjectRequest>() {
            @Override
            public void onProgress(GetObjectRequest request, long currentSize, long totalSize) {
                int progress = (int) (100 * currentSize / totalSize);
                LogUtils.i("下载进度: " + progress + "%");
                listener.onProgress(progress);
            }
        };
        // 异步上传时可以设置进度回调
        get.setProgressListener(callback2);
        getOss().asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                LogUtils.i("下载OK");
                try {
                    InputStream inputStream = result.getObjectContent();

                    FileOutputStream fileOut = new FileOutputStream(new File(savePath));
                    byte[] buf = new byte[1024 * 8];
                    while (true) {
                        int read = 0;
                        if (inputStream != null) {
                            read = inputStream.read(buf);
                        }
                        if (read == -1) {
                            break;
                        }
                        fileOut.write(buf, 0, read);
                    }
                    if (fileOut.getFD().valid()) {
                        LogUtils.i("获取文件保存成功");
                    } else {
                        LogUtils.i("获取文件失败");
                    }
                    listener.onSuccess(savePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientException, ServiceException serviceException) {
                LogUtils.i("下传失败");
                if (clientException != null) { // 本地异常如网络异常等
                    clientException.printStackTrace();
                }
                if (serviceException != null) {
                    serviceException.printStackTrace(); // 服务异常
                }
                listener.onFailure();
            }
        });
    }
}
*/
