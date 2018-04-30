package com.zzhserver.global;

import android.os.Environment;

/**
 * Created by Administrator on 2017/10/29 0029.
 */

public class Const {
    public static final String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final int PORT = 28975;
    //public static final String HOST = "192.168.0.106";
    public static final String HOST = "39.108.121.166";
    public static final String HOST_URL = "http://" + HOST + "/";
    public static final String IMG_URL = "http://" + HOST + "/img/";
    public static final String RECORD_URL = "http://" + HOST + "/record/";
    public static final String UP_FILE_URL = "http://" + HOST + "/upload_file.php";

    public static final String SP_LOGIN_NAME = "SP_LOGIN_NAME";//记住账号
    public static final String SP_LOGIN_PASSWORD = "SP_LOGIN_PASSWORD";//记住密码
    public static final String SP_LOGIN_UID = "SP_LOGIN_UID";//记住uid

    //Extra name
    public static final String TAG_TYPE = "TAG_TYPE";
    public static final String TAG_ID = "TAG_ID";
    public static final String TAG_NAME = "TAG_NAME";
    public static final String TAG_USER = "TAG_USER";
    public static final String TAG_GROUP = "TAG_GROUP";
    public static final String TAG_SEARCH = "TAG_SEARCH";
    public static final String TAG_PHOTO = "TAG_PHOTO";

    //客户端发送信息返回码
    public static final int SEND_OK = 1;
    public static final int SEND_FAIL = 0;

    //EventBus
    public static final int EVENT_ADD_USER_REQ = 1;//通知等待好友
    public static final int EVENT_ADD_USER_OK = 2;//通知同意好友
    public static final int EVENT_ADD_USER_REFUSE = 3;//通知拒绝好友
    public static final int EVENT_BE_ADD_USER_REQ = 11;//通知红点,别人请求
    public static final int EVENT_BE_ADD_USER_OK = 12;//通知红点,别人同意
    public static final int EVENT_BEADD_USER_REFUSE = 13;//通知红点,别人拒绝
    public static final int EVENT_STATUS_MSG = 4;//通知在线离线状态变化
    public static final int EVENT_GET_USER_LIST = 5;//通知用户列表
    public static final int EVENT_GET_USER_SEARCH_NOTIFY = 6;//通知搜索用户列表
    public static final int EVENT_GET_USER_SEARCH_END = 7;//通知搜索用户列表
    public static final int EVENT_DEL_USER = 8;//通知删除用户
    public static final int EVENT_OTHER_USER_CHANGE = 9;//有人更新用户信息

    public static final int EVENT_GET_GROUP_LIST = 41;//通知群组列表
    public static final int EVENT_GET_GROUP_USER_LIST = 42;//通知群组用户列表
    public static final int EVENT_CREATE_GROUP_SUCCESS = 43;//建立群组成功
    public static final int EVENT_CREATE_GROUP_FAIL = 44;//建立群组失败
    public static final int EVENT_MODIFY_GROUP_SUCCESS = 45;//修改群组成功
    public static final int EVENT_MODIFY_GROUP_FAIL = 46;//修改群组失败
    public static final int EVENT_DELETE_GROUP_SUCCESS = 47;//删除群组成功
    public static final int EVENT_DELETE_GROUP_FAIL = 48;//删除群组失败
    public static final int EVENT_GROUP_ADD_SUCCESS = 49;//群组加人成功
    public static final int EVENT_GROUP_ADD_FAIL = 50;//群组加人失败
    public static final int EVENT_GROUP_DEL_SUCCESS = 51;//群组踢人成功
    public static final int EVENT_GROUP_DEL_FAIL = 52;//群组踢人失败
    public static final int EVENT_MODIFY_USER_INFO_SUCCESS = 53;//修改信息成功
    public static final int EVENT_MODIFY_USER_INFO_FAIL = 54;//修改信息失败

    //*****************start客户端服务端共同类型*************
    //=====start传输类型相关=====
    //基本操作
    public static final int NOTIFY = 1;//通知
    public static final int GET_MY_USER = 2;//获取自己的用户信息
    public static final int GET_OFFLINE_MSG = 3;//获取离线信息
    public static final int SEND_MSG = 4;//发送信息
    public static final int USER_ONLINE = 5;//每次上线都要发
    public static final int USER_OFFLINE = 6;//自己下线,或者别人下线通知我
    public static final int INIT_ALL = 7;//初始化(刷新用户,群组,获取离线消息)
    public static final int SET_MY_USER = 8;//设置自己的信息
    public static final int UPDATE_USER = 9;//告诉别人需要更新信息
    //用户相关操作
    public static final int ADD_USER_REQ = 10;//添加用户好友
    public static final int ADD_USER_OK = 11;//添加用户好友
    public static final int ADD_USER_REFUSE = 12;//添加用户好友
    public static final int DEL_USER = 13;//删除用户好友
    public static final int BLACK_USER = 14;//拉黑用户好友
    public static final int GET_USER = 15;//根据id获取用户信息
    public static final int GET_USER_LIST = 16;//获取自己的用户列表
    public static final int GET_SEARCH_USER_SIZE = 17;//获取搜索用户的列表总数
    public static final int GET_SEARCH_USER_LIST = 18;//获取搜索用户的列表
    //群组相关操作
    public static final int CREATE_GROUP = 30;//建立新群组
    public static final int REMOVE_GROUP = 31;//移除群组
    public static final int MODIFY_GROUP = 32;//修改群组
    public static final int GET_GROUP_LIST = 34;//获取自己的群组列表
    public static final int GET_GROUP_USER_LIST = 35;//获取自己的群组列表
    public static final int GET_SEARCH_GROUP_SIZE = 36;//获取搜索的群组列表总数
    public static final int GET_SEARCH_GROUP_LIST = 37;//获取搜索的群组列表
    public static final int GROUP_ADD_USER = 38;//群组主动加用户
    public static final int GROUP_DEL_USER = 39;//群组删除用户
    public static final int JOIN_GROUP = 40;//用户加入群组
    public static final int QUIT_GROUP = 41;//用户退出群组
    //其他操作
    public static final int CALL_USER = 50;//单呼
    public static final int CALL_GROUP = 51;//群呼
    public static final int USER_OUT_CALL = 52;//有人离线通话了
    public static final int TEST = 90;
    //=====end传输类型相关=====

    //搜索类型
    public static final String TYPE_SEARCH_ID = "[SEARCH_ID]:";
    public static final String TYPE_SEARCH_NAME = "[SEARCH_NAME]:";
    public static final String TYPE_SEARCH_ONLINE = "[SEARCH_ONLINE]:";

    //添加好友 msgType字段
    //public static final int ADD_USER_REQ = 1;
    //public static final int ADD_USER_OK = 2;
    //public static final int ADD_USER_REFUSE = 3;
    public static final int ADD_USER_IGNORE = 4;

    public static final int STATUS_MSG = 100;
    //好友状态
    public static final int STATUS_WAIT_ADD = 0;//等待添加
    public static final int STATUS_BLACK_LIST = 1;//黑名单
    public static final int STATUS_WAIT_FRIEND = 2;//等待好友
    public static final int STATUS_REQ_FRIEND = 5;//别人申请好友
    public static final int STATUS_REFUSE_FRIEND = 3;//拒绝好友
    public static final int STATUS_OK_FRIEND = 4;//同意好友
    public static final int STATUS_OFFLINE = 6;//离线状态
    public static final int STATUS_WAIT_CALL = 7;//待通话状态
    public static final int STATUS_CALLING = 8;//通话状态
    public static final int STATUS_ONLINE = 12;//在线状态

    //账户操作类型
    public static final int ACCOUNT_LOGIN = 1;//登录
    public static final int ACCOUNT_REGISTER = 2;//注册
    public static final int ACCOUNT_MODIFY_PWD = 3;//修改密码
    public static final int ACCOUNT_MODIFY_INFO = 4;//修改信息

    public static final int NETWORK_FAIL = -1;//没有网络
    //登录返回码
    public static final int LOGIN_FAIL = 0;//登录失败
    public static final int LOGIN_SUCCESS = 1;//登录成功
    public static final int LOGIN_FAIL_UNREGISTER = 2;//未注册
    public static final int LOGIN_FAIL_PASSWORD_ERROR = 3;//密码错误

    //注册返回码
    public static final int REGISTER_FAIL = 0;//注册失败
    public static final int REGISTER_SUCCESS = 1;//注册成功
    public static final int REGISTER_ALREADY = 2;//已经注册

    //其他返回码
    public static final String SUCCESS = "1";//成功
    public static final String FAIL = "0";//失败
    //*****************end客户端服务端共同类型*************
}
