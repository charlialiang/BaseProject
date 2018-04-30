package com.zzhserver.manager;

import android.net.TrafficStats;

import com.google.gson.Gson;
import com.zzhserver.global.App;
import com.zzhserver.main.setting.SettingModel;
import com.zzhserver.global.HeaderClientInterceptor;
import com.zzhserver.main.chat.ChatModel;
import com.zzhserver.main.group.GroupModel;
import com.zzhserver.main.InfoModel;
import com.zzhserver.main.user.UserModel;
import com.zzhserver.main.user.req.UserReqModel;
import com.zzhserver.main.user.search.UserSearchModel;
import com.zzhserver.pojo.bean.MessageBean;
import com.zzhserver.pojo.bean.MessageData;
import com.zzhserver.pojo.db.DUser;
import com.zzhserver.protobuf.*;
import com.zzhserver.global.Const;
import com.zzhserver.pojo.bean.LoginBean;
import com.zzhserver.utils.LogUtils;
import com.zzhserver.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

/**
 * Created by Administrator on 2017/6/3 0003.
 */

public class GrpcManager {
    private static GrpcManager instance;

    private final GroupModel groupManager;
    private final UserModel userManager;
    private final ChatModel chatManager;
    private ManagedChannel mChannel;
    private GreeterGrpc.GreeterBlockingStub mBlockingStub;//同步
    private GreeterGrpc.GreeterStub mAsyncStub;//非阻塞,异步存根
    private StreamObserver<Message> mSendStream;//主动发送
    private StreamObserver<Message> mReceiveObserver;//监听接收
    private Gson gson = new Gson();
    Map<String, String> headerMap = new HashMap<>();
    //HeaderClientInterceptor interceptor;

    private GrpcManager() {
        //interceptor = new HeaderClientInterceptor(headerMap);
        userManager = UserModel.INSTANCE;
        chatManager = ChatModel.getInstance();
        groupManager = GroupModel.getInstance();
    }

    public static GrpcManager getInstance() {
        if (instance == null) {
            synchronized (GrpcManager.class) {
                if (instance == null) {
                    instance = new GrpcManager();
                }
            }
        }
        return instance;
    }

    //开启同步传输通道
    public void blockingStub() {
        LogUtils.i("blockingStub()");
        mChannel = ManagedChannelBuilder.forAddress(Const.HOST, Const.PORT)
                .usePlaintext(true)
                .intercept(new HeaderClientInterceptor())
                .build();
        mBlockingStub = GreeterGrpc.newBlockingStub(mChannel);
    }

    //开启异步传输通道
    public void asyncStub(String info) {
        LogUtils.i("asyncStub()异步传输:" + info);
        if (mChannel == null) {
            mChannel = ManagedChannelBuilder.forAddress(Const.HOST, Const.PORT)
                    .usePlaintext(true)
                    .build();
        }
        if (mAsyncStub == null) {
            mAsyncStub = GreeterGrpc.newStub(mChannel);
            mReceiveObserver = receiveStream();//初始化监听接收通道
            mSendStream = mAsyncStub.sendStream(mReceiveObserver);//初始化主动发送通道
        }
        //LogUtils.i("mSendStream:" + mSendStream );
            /*if (mReceiveObserver == null || mSendStream == null) {
            //receiveReply = ServerReply.newBuilder().setRet(0).setUid(InfoManager.getInstance().getUid()).build();
            //mAsyncStub.sendServer(receiveReply, mReceiveObserver);
            }*/
    }

    //关闭传输通道
    public void shutdownChannel() {
        try {
            LogUtils.i("关闭通道  mChannel1 = " + mChannel);
            if (mChannel != null) {
                mChannel.shutdown();
                mChannel = null;
            }
            mBlockingStub = null;
            mAsyncStub = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ManagedChannel getChannel() {
        return mChannel;
    }

    public LoginBean login(String username, String password) {
        getFlow("登录");
        //LogUtils.i("登录是主线程:" + (Looper.myLooper() == Looper.getMainLooper()));
        LoginBean loginBean = new LoginBean();
        if (!NetUtils.isConnected()) {
            loginBean.code = Const.NETWORK_FAIL;//返回无网络
            return loginBean;
        }
        loginBean.code = Const.LOGIN_FAIL;//默认失败
        try {
            blockingStub();
            AccountReq request = AccountReq.newBuilder().setType(Const.ACCOUNT_LOGIN).setUsername(username).setPassword(password).build();
            AccountReply reply = mBlockingStub.accountOption(request);
            loginBean.code = reply.getCode();
            loginBean.uid = reply.getUid();
        } catch (Exception e) {
            e.printStackTrace();
            shutdownChannel();
            return loginBean;
        }
        return loginBean;
    }

    public int register(String username, String password, String name) {
        int result = Const.NETWORK_FAIL;//没网络返回无网络
        if (!NetUtils.isConnected()) {
            return result;//返回无网络
        }
        result = Const.REGISTER_FAIL;//默认失败
        try {
            blockingStub();
            AccountReq request = AccountReq.newBuilder().setType(Const.ACCOUNT_REGISTER).setUsername(username).setPassword(password).setExtra(name).build();
            AccountReply reply = mBlockingStub.accountOption(request);
            result = reply.getCode();
        } catch (Exception e) {
            e.printStackTrace();
            shutdownChannel();
            return result;//默认失败
        }
        return result;
    }

    //==================start发送内容==================
    //proto字段不能跳过某个字段不赋值!!!!!!
    //发送消息
    public void sendMessage(MessageBean messageBean) {
        getFlow("发消息");
        if (!NetUtils.isConnected()) {
            chatManager.retSendMsgRet(Const.SEND_FAIL, messageBean.time);
            return;
        }
        asyncStub("发送出去信息:" + messageBean.toString());
        Message message = Message.newBuilder().setTyp(Const.SEND_MSG).setSid(messageBean.sid).setTid(messageBean.tid)
                .setTime(messageBean.time).setMTyp(messageBean.mTyp).setMsg(messageBean.msg).build();
        //LogUtils.i("mSendStream = " + mSendStream + "mReceiveObserver = " + mReceiveObserver);
        onSend(message);
    }

    //获取我的信息
    public void getMyUserInfo(int uid) {
        if (uid < 1) {
            return;
        }
        asyncStub("获取我的信息");
        onSend(Message.newBuilder().setTyp(Const.GET_MY_USER).setSid(uid).build());
    }

    //设置我的信息
    public void setMyUserInfo(String name, String headPic) {
        asyncStub("设置我的信息");
        onSend(Message.newBuilder().setTyp(Const.SET_MY_USER).setSid(InfoModel.INSTANCE.getUid()).setMsg(name).setX(headPic == null ? "" : headPic).build());
    }

    //初始化(刷新用户,群组,获取离线消息)
    public void initAll() {
        getFlow("初始化(刷新用户,群组,获取离线消息)");
        asyncStub("初始化(刷新用户,群组,获取离线消息)");
        onSend(Message.newBuilder().setTyp(Const.INIT_ALL).setSid(InfoModel.INSTANCE.getUid()).build());
    }

    //获取好友用户信息
    public void getUserList() {
        getFlow("获取用户列表");
        asyncStub("获取用户列表");
        onSend(Message.newBuilder().setTyp(Const.GET_USER_LIST).setSid(InfoModel.INSTANCE.getUid()).build());
    }

    //获取单个群组信息
    public void getGroup(int gid) {
        //getFlow("获取群组信息");
        asyncStub("获取群组信息");
        onSend(Message.newBuilder().setTyp(Const.GET_GROUP_LIST).setSid(InfoModel.INSTANCE.getUid()).setTid(gid).build());
    }

    //获取群组用户列表信息
    public void getGroupUserList(int gid) {
        //getFlow("获取群组信息");
        asyncStub("获取群组列表信息");
        onSend(Message.newBuilder().setTyp(Const.GET_GROUP_USER_LIST).setSid(InfoModel.INSTANCE.getUid()).setTid(gid).build());
    }

    //获取离线信息
    public void getOfflineMsg() {
        asyncStub("获取离线信息");
        onSend(Message.newBuilder().setTyp(Const.GET_OFFLINE_MSG).setSid(InfoModel.INSTANCE.getUid()).build());
    }

    //按条件搜索用户列表总数 msg:搜索条件 //[SEARCH_ID]:123456    [SEARCH_NAME]:张三
    public void getSearchUserListSize(String msg) {
        asyncStub("按条件搜索用户列表总数");
        onSend(Message.newBuilder().setTyp(Const.GET_SEARCH_USER_SIZE).setSid(InfoModel.INSTANCE.getUid()).setMsg(msg).build());
    }

    //按条件搜索用户列表 msg:搜索条件 //[SEARCH_ID]:123456    [SEARCH_NAME]:张三
    public void getSearchUserList(String msg) {
        getFlow("按条件搜索用户列表");
        asyncStub("按条件搜索用户列表:" + msg);
        onSend(Message.newBuilder().setTyp(Const.GET_SEARCH_USER_LIST).setSid(InfoModel.INSTANCE.getUid()).setMsg(msg).build());
    }

    //请求加用户好友
    //msgType:1发出邀请好友; 2回应确认好友; 3回应拒绝好友; 4忽略邀请
    public void sendAddUserReq(int tagId) {
        asyncStub("邀请好友Req:" + tagId);
        onSend(Message.newBuilder().setTyp(Const.ADD_USER_REQ).setSid(InfoModel.INSTANCE.getUid()).setTid(tagId).build());
    }

    //请求加用户好友
    //msgType:1发出邀请好友; 2回应确认好友; 3回应拒绝好友; 4忽略邀请
    public void sendAddUserOk(int tagId) {
        asyncStub("邀请好友Ok:" + tagId);
        onSend(Message.newBuilder().setTyp(Const.ADD_USER_OK).setSid(InfoModel.INSTANCE.getUid()).setTid(tagId).build());
    }

    public void sendAddUserRefuse(int tagId) {
        asyncStub("邀请好友Refuse:" + tagId);
        onSend(Message.newBuilder().setTyp(Const.ADD_USER_REFUSE).setSid(InfoModel.INSTANCE.getUid()).setTid(tagId).build());
    }

    //删除好友
    public void sendDelUser(int tagId) {
        asyncStub("删除好友:" + tagId);
        onSend(Message.newBuilder().setTyp(Const.DEL_USER).setSid(InfoModel.INSTANCE.getUid()).setTid(tagId).build());
    }

    //拉黑名单
    public void sendBlackListUser(int tagId) {
        asyncStub("拉黑名单:" + tagId);
        onSend(Message.newBuilder().setTyp(Const.BLACK_USER).setSid(InfoModel.INSTANCE.getUid()).setTid(tagId).build());
    }

    // 建立群组
    public void sendCreateGroup(String name, String headPic) {
        asyncStub("建立群组");
        onSend(Message.newBuilder().setTyp(Const.CREATE_GROUP).setSid(InfoModel.INSTANCE.getUid()).setMsg(name).setX(headPic == null ? "" : headPic).build());
    }

    //删除群组
    public void sendRemoveGroup(int gid) {
        asyncStub("删除群组:" + gid);
        onSend(Message.newBuilder().setTyp(Const.REMOVE_GROUP).setSid(InfoModel.INSTANCE.getUid()).setTid(gid).build());
    }

    // 修改群组
    public void sendModifyGroup(int gid, String name, String headPic) {
        asyncStub("修改群组");
        onSend(Message.newBuilder().setTyp(Const.MODIFY_GROUP).setSid(InfoModel.INSTANCE.getUid()).setTid(gid).setMsg(name).setX(headPic == null ? "" : headPic).build());
    }

    // 请求群组加人
    public void sendGroupAddUser(int gid, String uidListStr) {
        asyncStub(gid + "群组加人:" + uidListStr);
        onSend(Message.newBuilder().setTyp(Const.GROUP_ADD_USER).setSid(InfoModel.INSTANCE.getUid()).setTid(gid).setMsg(uidListStr).build());
    }

    //群组踢人
    public void sendGroupDelUser(int gid, int tagId) {
        asyncStub(gid + "群组踢人:" + tagId);
        onSend(Message.newBuilder().setTyp(Const.GROUP_DEL_USER).setSid(InfoModel.INSTANCE.getUid()).setTid(gid).setMsg(String.valueOf(tagId)).build());
    }

    // 主动请求加入群组
    public void sendJoinGroup(int gid) {
        asyncStub("主动请求加入群组:" + gid);
        onSend(Message.newBuilder().setTyp(Const.JOIN_GROUP).setSid(InfoModel.INSTANCE.getUid()).setTid(gid).build());
    }

    //主动请求退出群组
    public void sendQuitGroup(int gid) {
        asyncStub("主动请求退出群组:" + gid);
        onSend(Message.newBuilder().setTyp(Const.QUIT_GROUP).setSid(InfoModel.INSTANCE.getUid()).setTid(gid).build());
    }

    //测试最小字节
    public void sendTest() {
        //asyncStub("测试最小字节");
        getFlow("测试最小字节");
        onSend(Message.newBuilder().setTyp(Const.TEST).setSid(InfoModel.INSTANCE.getUid()).setTime(999).setTid(999).setMsg("").build());
    }

    private void onSend(final Message message) {
        //LogUtils.i("当前线程:" + Thread.currentThread().getName());
        //mSendStream.onNext(message);
        synchronized (instance) {
            App.getInstance().getGrpcThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    LogUtils.i("当前线程:" + Thread.currentThread().getName());
                    mSendStream.onNext(message);
                }
            });
        }
    }

    //==================end发送内容==================

    //==================start接收内容==================
    //接收的Stream
    public StreamObserver<Message> receiveStream() {
        mReceiveObserver = new StreamObserver<Message>() {
            @Override
            public void onNext(Message message) {
                LogUtils.i("接消息：" + message.toString() + ",大小:" + message.toString().getBytes().length);

                switch (message.getTyp()) {//服务器回传收到某消息等通知
                    case Const.NOTIFY:
                        doNotify(message);
                        return;
                    case Const.SEND_MSG://其他人发给我的消息
                        recMessage(message);
                        onSend((Message.newBuilder().setTyp(Const.NOTIFY).setMTyp(Const.SEND_MSG).setSid(InfoModel.INSTANCE.getUid()).setTid(message.getSid()).build()));
                        break;
                    case Const.ADD_USER_REQ://被其他人请求我加好友
                        UserReqModel.INSTANCE.recAddUserReq(message.getMsg());
                        onSend((Message.newBuilder().setTyp(Const.NOTIFY).setMTyp(Const.ADD_USER_REQ).setSid(InfoModel.INSTANCE.getUid()).setTid(message.getSid()).build()));
                        break;
                    case Const.ADD_USER_OK://被其他人同意我好友
                        UserReqModel.INSTANCE.recAddUserOk(message.getSid());
                        break;
                    case Const.ADD_USER_REFUSE://被其他拒绝我好友
                        UserReqModel.INSTANCE.recAddUserRefuse(message.getSid());
                        onSend((Message.newBuilder().setTyp(Const.NOTIFY).setMTyp(Const.ADD_USER_REFUSE).setSid(InfoModel.INSTANCE.getUid()).setTid(message.getSid()).build()));
                        break;
                    case Const.DEL_USER://被其他人删除我
                        userManager.recDelUser(message.getSid());
                        break;
                    case Const.BLACK_USER://被其他人拉黑我
                        break;
                    case Const.USER_ONLINE://服务器通知有人上线了
                        userManager.userOnlineOffline(Const.STATUS_ONLINE, message.getSid());
                        break;
                    case Const.USER_OFFLINE://服务器通知有人下线了
                        userManager.userOnlineOffline(Const.STATUS_OFFLINE, message.getSid());
                        break;
                    case Const.GET_OFFLINE_MSG://获取离线信息
                        recOfflineMsg(message.getMsg());
                        break;
                    case Const.GET_USER://获取<他人用户资料>
                        userManager.recOtherUser(message.getMsg());
                        break;
                    case Const.GET_MY_USER://获取<个人用户信息>
                        recMyUser(message);
                        break;
                    case Const.SET_MY_USER://获取<设置后个人用户信息>
                        SettingModel.INSTANCE.retModify(message.getMsg());
                        break;
                    case Const.GET_USER_LIST://获取<用户列表>
                        userManager.recServerUserList(message.getMsg());
                        break;
                    case Const.GET_SEARCH_USER_LIST://获取<搜索用户列表>
                        UserSearchModel.INSTANCE.recUserSearchList(message.getMsg());
                        break;
                    case Const.GET_SEARCH_USER_SIZE://获取<搜索用户列表总数>
                        UserSearchModel.INSTANCE.recUserSearchListSize(message.getMsg());
                        break;
                    case Const.GET_GROUP_LIST://获取<群组列表>
                        groupManager.recGroupList(message.getMsg());
                        break;
                    case Const.GET_GROUP_USER_LIST://获取<群组用户列表>
                        groupManager.recGroupUserList(message.getMsg(), message.getTid());
                        break;
                    case Const.CREATE_GROUP://获取<创建群组>id
                        groupManager.retCreateGroup(message.getMsg());
                        break;
                    case Const.MODIFY_GROUP://获取<修改群组>id
                        groupManager.retModifyGroup(message.getMsg());
                        break;
                    case Const.REMOVE_GROUP://获取<删除群组>id
                        groupManager.retRemoveGroup(message.getMsg(), message.getTid());
                        break;
                    case Const.GROUP_ADD_USER://发送结果
                        groupManager.retGroupDoUser(message.getMsg(), Const.GROUP_ADD_USER);
                        break;
                    case Const.GROUP_DEL_USER://发送结果
                        groupManager.retGroupDoUser(message.getMsg(), Const.GROUP_DEL_USER);
                        break;
                    case Const.JOIN_GROUP://发送结果
                        groupManager.retJoinGroup(message.getMsg());
                        break;
                    case Const.QUIT_GROUP://发送结果
                        groupManager.retQuitGroup(message.getMsg(), message.getTid());
                        break;

                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                shutdownChannel();
            }

            @Override
            public void onCompleted() {

            }
        };
        return mReceiveObserver;
    }

    private void doNotify(Message message) {
        LogUtils.i("收到了通知 类型：" + message.getMTyp());
        switch (message.getMTyp()) {
            case Const.SEND_MSG://发送结果
                chatManager.retSendMsgRet(Const.SEND_OK, message.getTime());
                break;
            case Const.ADD_USER_REQ://发送结果
                UserReqModel.INSTANCE.retAddUserReq(message.getTid());
                break;
            case Const.ADD_USER_OK://发送结果
                UserReqModel.INSTANCE.retAddUserOk(message.getTid());
                break;
            case Const.ADD_USER_REFUSE://发送结果
                UserReqModel.INSTANCE.retAddUserRefuse(message.getTid());
                break;
            case Const.DEL_USER://发送结果
                userManager.retDelUser(message.getTid());
                break;

            /*case Const.USER_ONLINE://服务器通知有人上线了
                userManager.userOnlineOffline(Const.STATUS_ONLINE, message.getSrcId());
                break;
            case Const.USER_OFFLINE://服务器通知有人下线了
                userManager.userOnlineOffline(Const.STATUS_OFFLINE, message.getSrcId());
                break;
            case Const.GET_OFFLINE_MSG://获取离线信息
                recOfflineMsg(message.getMsg());
                break;
            case Const.GET_USER://获取<他人用户资料>
                recOtherUser(message);
                break;
            case Const.GET_MY_USER://获取<个人用户资料>
                recMyUser(message);
                break;
            case Const.GET_USER_LIST://获取<用户列表>
                userManager.recServerUserList(message.getMsg());
                break;
            case Const.GET_SEARCH_USER_LIST://获取<搜索用户列表>
                userManager.recUserSearchList(message.getMsg());
                break;
            case Const.GET_SEARCH_USER_SIZE://获取<搜索用户列表总数>
                userManager.recUserSearchListSize(message.getMsg());
                break;*/
        }
    }

    private void recMyUser(Message message) {
        try {
            DUser dUser = gson.fromJson(message.getMsg(), DUser.class);
            InfoModel.INSTANCE.setMyUser(dUser);
            initAll();//初始化(刷新用户,群组,获取离线消息)
            //getUserArray();//第一次上线刷新用户好友列表
            //getGroupArray();//第一次上线刷新群组列表
            //getOfflineMsg();//上线获取离线消息
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void recOfflineMsg(String msg) {
        try {
            MessageData messageData = gson.fromJson(msg, MessageData.class);
            LogUtils.i("离线信息:" + messageData);
            for (MessageBean message : messageData.messageList) {
                if (message.typ == Const.SEND_MSG) {
                    chatManager.recMessage(message);
                } else if (message.typ == Const.ADD_USER_REQ) {
                    UserReqModel.INSTANCE.recAddUserReq(message.msg);
                } else if (message.typ == Const.ADD_USER_OK) {
                    UserReqModel.INSTANCE.recAddUserOk(message.sid);
                } else if (message.typ == Const.ADD_USER_REFUSE) {
                    UserReqModel.INSTANCE.recAddUserRefuse(message.sid);
                } else if (message.typ == Const.DEL_USER) {
                    userManager.recDelUser(message.tid);
                }
            }
            //收到了消息,回复服务器SEND_OK
            Message msgOk = Message.newBuilder().setSid(InfoModel.INSTANCE.getUid()).setTyp(Const.NOTIFY).setMTyp(Const.GET_OFFLINE_MSG).build();
            mSendStream.onNext(msgOk);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void recMessage(Message message) {
        MessageBean messageBean = new MessageBean();
        messageBean.msg = message.getMsg();
        messageBean.mTyp = message.getMTyp();
        messageBean.typ = message.getTyp();
        messageBean.sid = message.getSid();
        messageBean.tid = message.getTid();
        messageBean.time = message.getTime();
        chatManager.recMessage(messageBean);
    }
    //==================end接收内容==================


    public void getFlow(final String s) {
        final long rx = TrafficStats.getUidRxBytes(android.os.Process.myUid());
        final long tx = TrafficStats.getUidTxBytes(android.os.Process.myUid());
        LogUtils.i(s + "流量: rx=" + rx + "B  ,tx=" + tx + "B");
    }

                        /*//发送消息
                        synchronized public void sendMessage(final ChatBean chatBean) {
                        asyncStub();
                        LogUtils.w("发送出去信息:" + chatBean.toString());
                        StreamObserver<ChatMessage> requestObserver = mAsyncStub.sendClient(new StreamObserver<ServerReply>() {
                            @Override
                            public void onSend(ServerReply reply) {
                            LogUtils.w("发送出去信息回应:" + reply.getRet());
                            chatPresenter.retSendMsgRet(reply.getRet(), chatBean.getMsgTime());
                            }

                            @Override
                            public void onError(Throwable t) {
                            LogUtils.e("发送出去信息错误:" + t.getMessage());
                            chatPresenter.retSendMsgRet(ChatBean.SEND_FAIL, chatBean.getMsgTime());
                            }

                            @Override
                            public void onCompleted() {
                            LogUtils.w("发送出去信息完成");
                            chatPresenter.retSendMsgRet(ChatBean.SEND_OK, chatBean.getMsgTime());
                            }
                            });
                            ChatMessage chatMessage = ChatMessage.newBuilder().setTx(chatBean.getTx()).setMsgTime(chatBean.getMsgTime())
                            .setMsgType(chatBean.getMsgType()).setSrcId(chatBean.getSrcId()).setTagId(chatBean.getTagId()).build();
                            requestObserver.onSend(chatMessage);
                            requestObserver.onCompleted();
                            }

                            //接收的消息
                            public StreamObserver<ChatMessage> receiveStream() {
                                mReceiveObserver = new StreamObserver<ChatMessage>() {
                                    @Override
                                    public void onSend(ChatMessage chatMessage) {
                                    ChatBean chatBean = new ChatBean();
                                    chatBean.setTx(chatMessage.getTx());
                                    chatBean.setMsgTime(chatMessage.getMsgTime());
                                    chatBean.setMsgType(chatMessage.getMsgType());
                                    chatBean.setTagId(chatMessage.getTagId());
                                    chatBean.setSrcId(chatMessage.getSrcId());
                                    chatBean.setSendOrReceive(ChatBean.RECEIVE);
                                    chatBean.setUnread(true);

                                    LogUtils.w("发来的信息:" + chatBean.toString());
                                    chatPresenter.recMessage(chatBean);
                                    receiveReply = ServerReply.newBuilder().setRet(ChatBean.SEND_OK).setUid(InfoManager.getInstance().getUid()).build();
                                    mAsyncStub.sendServer(receiveReply, mReceiveObserver);
                                    }

                                    @Override
                                    public void onError(Throwable t) {
                                    LogUtils.e("发来的信息错误:" + t.getMessage());
                                    receiveReply = ServerReply.newBuilder().setRet(ChatBean.SEND_FAIL).setUid(InfoManager.getInstance().getUid()).build();
                                    mAsyncStub.sendServer(receiveReply, mReceiveObserver);
                                    }

                                    @Override
                                    public void onCompleted() {
                                    LogUtils.w("发来的信息完成");
                                    receiveReply = ServerReply.newBuilder().setRet(1).setUid(InfoManager.getInstance().getUid()).build();
                                    mAsyncStub.sendServer(receiveReply, mReceiveObserver);
                                    }
                                    };
                                    return mReceiveObserver;
                                    }*/
}




