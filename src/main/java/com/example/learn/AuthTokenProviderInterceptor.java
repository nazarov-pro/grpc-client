package com.example.learn;

import io.grpc.*;

public class AuthTokenProviderInterceptor implements ClientInterceptor {
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            @Override
            public void start(final Listener<RespT> responseListener, final Metadata headers) {
                headers.put(Metadata.Key.of("auth_token", Metadata.ASCII_STRING_MARSHALLER), "admin_token");
                super.start(responseListener, headers);
            }
        };
    }
}
