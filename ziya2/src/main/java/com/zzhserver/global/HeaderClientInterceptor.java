package com.zzhserver.global;

import com.google.common.annotations.VisibleForTesting;
import com.zzhserver.utils.LogUtils;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.ForwardingClientCallListener.SimpleForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;

/**
 * A interceptor to handle client header.
 */
public class HeaderClientInterceptor implements ClientInterceptor {


    @VisibleForTesting
    static final Metadata.Key<String> CUSTOM_HEADER_KEY =
            Metadata.Key.of("custom_client_header_key", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method,
                                                               CallOptions callOptions, Channel next) {

        LogUtils.i("next:" + next.toString());
        return new SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {//发出去的头
                //加入头
                //headers.put(CUSTOM_HEADER_KEY, "customRequestValue");
                super.start(new SimpleForwardingClientCallListener<RespT>(responseListener) {
                    @Override
                    public void onHeaders(Metadata headers) {//服务器过来的头
                        LogUtils.i("header received from server:" + headers);
                        super.onHeaders(headers);
                    }
                }, headers);

            }
        };
    }
}